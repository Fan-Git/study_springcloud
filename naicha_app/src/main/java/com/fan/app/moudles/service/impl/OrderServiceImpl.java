package com.fan.app.moudles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.app.moudles.mapper.GoodsMapper;
import com.fan.app.moudles.mapper.OrderMapper;
import com.fan.app.moudles.service.OrderService;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderCloseResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.fan.common.common.config.property.WeixinProperty;
import com.fan.common.common.constant.Const;
import com.fan.common.common.enums.OrderStatus;
import com.fan.common.common.enums.OrderTakeType;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.common.util.GeneratorUtil;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.OrderInfo;
import com.fan.common.entity.app.form.CreateOrderForm;
import com.fan.common.entity.app.vo.HistoryOrderVO;
import com.fan.common.entity.common.AppConfig;
import com.fan.common.entity.common.RecentOrderMessage;
import com.fan.common.service.AppConfigService;
import com.fan.common.service.LockService;
import com.fan.common.service.OrderMessageService;
import com.fan.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private RedisService redisService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private OrderMessageService orderMessageService;
    @Resource
    private WeixinProperty weixinProperty;
    @Resource
    private WxPayService wxPayService;
    @Resource
    private AppConfigService appConfigService;
    @Resource
    private LockService lockService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createOrder(CreateOrderForm orderParams, String wxOpenid) {
        if (orderParams.getTotalPrice() < 0)
            return Result.fail("订单的价格异常");
        if (orderParams.getGoodsTotalNum() <= 0)
            return Result.fail("不能生成空订单，请选择商品");
        // 外卖配送 检验参数
        if (OrderTakeType.ENUM_take_out.value.equals(orderParams.getTakeType()))
            if (StringUtils.isEmpty(orderParams.getAddressDetail())
                    || orderParams.getAddressDetail().trim().length() < 3 // 尽量确保收货地址等正确性
                    || StringUtils.isEmpty(orderParams.getUserPhone())
                    || StringUtils.isEmpty(orderParams.getReceiver()))
                return Result.fail("收货信息错误, 请检查收获地址、手机和姓名");

        AppConfig appConfig = appConfigService.getAppConfig();
        if (!appConfig.getShopStatus()) {
            return Result.fail("商家休息中，下单失败");
        }
        Date currentTime = new Date();
        int nowHour = LocalTime.now(ZoneId.of("Asia/Shanghai")).getHour();
        if (!(nowHour >= appConfig.getBusinessStartTime() && nowHour < appConfig.getBusinessEndTime()))
            return Result.fail("未到营业时间，下单失败");

        if (!lockService.tryLock(Const.CONST_lock_redis_prefix + wxOpenid, "", 15))
            return Result.fail("正在下单中，请勿重复下单");

        try {
            // TODO 数据库没有订单商品表，将订单商品的信息全部以字符串(奶茶1[加冰 中杯 少量糖 ]*1)这样的方式存在订单表里，价格也是让小程序里去算
            OrderInfo orderInfo = new OrderInfo();
            BeanUtils.copyProperties(orderParams, orderInfo);
            orderInfo.setWxOpenid(wxOpenid);
            orderInfo.setOrderNo(GeneratorUtil.generateOrderNo());
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_not_pay_money.value);
            orderInfo.setCreateTime(currentTime);
            orderInfo.setGoodsPreview(orderParams.getGoodsPreview());
            orderInfo.setGoodsTotalNum(orderParams.getGoodsTotalNum());
            orderInfo.setTotalPrice(orderParams.getTotalPrice());
            orderInfo.setPayPrice(null); // 实际支付价格用微信回调去调
            orderMapper.insert(orderInfo);

            // TODO 通知订单消息, 可以使用redis或者rabbitMQ
            RecentOrderMessage message = new RecentOrderMessage();
            message.setOrderNo(orderInfo.getOrderNo());
            message.setAddress(orderInfo.getAddressDetail());
            orderMessageService.addOrderMessage(message);
            return Result.ok("下单成功", orderInfo.getOrderNo());
        } finally {
            lockService.releaseLock(Const.CONST_lock_redis_prefix + wxOpenid);
        }
    }


    /**
     * 通过微信通用下单接口API成功获取预支付交易会话标识（prepay_id）后，需要通过小程序调起支付API来调起微信支付收银台
     *
     * @param ip 小程序调起收银台
     *           wx.requestPayment
     *           ({
     *           "timeStamp": "1414561699",
     *           "nonceStr": "5K8264ILTKCH16CQ2502SI8ZNMTM67VS",
     *           "package": "prepay_id=wx201410272009395522657a690389285100",
     *           "signType": "RSA",
     *           "paySign": "oR9d8PuhnIc+YZ8cBHFCwfgpaK9gd7vaRvkYD7rthRAZ\/X+QBhcCYL21N7cHCTUxbQ+EAt6Uy+lwSN22f5YZvI45MLko8Pfso0jm46v5hqcVwrk6uddkGuT+Cdvu4WBqDzaDjnNa5UK3GfE1Wfl2gHxIIY5lLdUgWFts17D4WuolLLkiFZV+JSHMvH7eaLdT9N5GBovBwu5yYKUR7skR8Fu+LozcSqQixnlEZUfyE55feLOQTUYzLmR9pNtPbPsu6WVhbNHMS3Ss2+AehHvz+n64GDmXxbX++IOBvm2olHu3PsOUGRwhudhVf7UcGcunXt8cqNjKNqZLhLw4jq\/xDg==",
     *           "success":function(res){},
     *           "fail":function(res){},
     *           "complete":function(res){}
     *           })
     */
    // 微信小程序预先支付的统一订单接口，返回的微信支付需要的参数, TODO 可以通过自己的orderNo去查询有没有支付  回调或定时任务去轮询
    @Override
    public Object wxPrepay(String wxOpenid, String orderNo, String ip) throws ServiceException {
        Integer totalPrice = orderMapper.getOrderTotalPriceByOrderNo(orderNo);
        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            // 设置微信请求基本信息
            orderRequest.setAppid(weixinProperty.getAppid());
            orderRequest.setMchId(weixinProperty.getMchId());
            orderRequest.setOutTradeNo(orderNo); // 商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一
            orderRequest.setBody("Fan茶"); // 示例值：Image形象店-深圳腾大-QQ公仔
            Date expireDate = new Date(System.currentTimeMillis() + 24 * 3600 * 1000);
            log.info("the order expireDate is: " + expireDate);
            orderRequest.setTimeExpire(new SimpleDateFormat("yyyyMMddHHmmss").format(expireDate));
            orderRequest.setTotalFee(totalPrice); // 订单总金额，单位为分
            orderRequest.setOpenid(wxOpenid);
            orderRequest.setSpbillCreateIp(ip); // APP和网页支付提交用户端ip
            orderRequest.setTradeType(WxPayConstants.TradeType.JSAPI); // JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
            return wxPayService.createOrder(orderRequest);
        } catch (WxPayException e) {
            log.error("[微信支付] 异常", e);
            throw ServiceException.CONST_weixin_pay_exception;
        } catch (Exception e) {
            log.error("[预付款异常]", e);
            throw ServiceException.CONST_weixin_pay_exception;
        }
    }

    @Override
    public Page<HistoryOrderVO> getHistoryOrderByPage(Integer pageNo, Integer pageSize, String wxOpenid) throws ServiceException {
        Page<HistoryOrderVO> page = new Page<>(pageNo, pageSize);
        page.setRecords(orderMapper.getHistoryOrderByPage(wxOpenid, (pageNo - 1) * pageSize, pageSize));
        page.setTotal(orderMapper.getHistoryOrderTotalCount(wxOpenid));
        return page;
    }

    @Override
    public OrderInfo getOrderDetail(String orderNo) throws ServiceException {
        return orderMapper.selectById(orderNo);
    }

    // 取消订单，用户在未支付的情况下才能取消订单
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer cancelOrder(String orderNo) throws WxPayException {
        OrderInfo orderInfo = orderMapper.selectById(orderNo);
        if (!OrderStatus.ENUM_has_not_pay_money.value.equalsIgnoreCase(orderInfo.getOrderStatus()))
            return 0;
        WxPayOrderCloseResult result = wxPayService.closeOrder(orderNo);
        if (result.getResultCode().equals(WxPayConstants.ResultCode.SUCCESS) && result.getResultCode().equals(WxPayConstants.ResultCode.SUCCESS))
            return orderMapper.deleteById(orderNo); // 删除订单
        return 0;
    }

    // 用户确认收货
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer finishedOrder(String orderNo, String wxOpenid) throws ServiceException {
        OrderInfo orderInfo = orderMapper.selectById(orderNo);
        // '请取餐' 或 '已送达' 时用户才能让去确认收货
        if (OrderStatus.ENUM_please_take_meal.value.equalsIgnoreCase(orderInfo.getOrderStatus())
                || OrderStatus.ENUM_has_received.value.equalsIgnoreCase(orderInfo.getOrderStatus()))
            return orderMapper.finishOrder(orderNo, OrderStatus.ENUM_has_completed.value, new Date());

        throw ServiceException.CONST_confirm_receive_failed;
    }

    // 获取正在处理的订单
    @Override
    public List<OrderInfo> getHandlingOrders(String wxOpenid) throws ServiceException {
        List<String> params = new ArrayList<String>() {{
            add(OrderStatus.ENUM_has_completed.value);
            add(OrderStatus.ENUM_has_canceled.value);
            add(OrderStatus.ENUM_has_refunded.value);
            add(OrderStatus.ENUM_on_refunding.value);
        }};

        return orderMapper.selectList(new QueryWrapper<OrderInfo>()
                .eq("wx_openid", wxOpenid)
                .notIn("order_status", params)
                .orderByDesc("create_time"));
    }
}
