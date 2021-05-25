package com.system.general.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.fan.common.common.constant.Const;
import com.fan.common.common.enums.GoodsPropertyCategory;
import com.fan.common.common.enums.OrderStatus;
import com.fan.common.entity.app.GoodsProperty;
import com.fan.common.entity.app.OrderInfo;
import com.fan.common.entity.app.dto.GoodsDTO;
import com.fan.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.system.general.mapper.OrderInfoAdminMapper;
import com.system.general.mapper.GoodsAdminMapper;
import com.system.general.mapper.GoodsPropertyAdminMapper;
import com.system.general.service.OrderInfoAdminService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务
 */
@EnableScheduling
@Slf4j
@Component
@ComponentScan("com.system.general.**")
public class TimingTaskRunner {
    @Resource
    private OrderInfoAdminMapper orderInfoAdminMapper;
    @Resource
    private OrderInfoAdminService orderInfoAdminService;
    @Resource
    private GoodsAdminMapper goodsAdminMapper;
    @Resource
    private GoodsPropertyAdminMapper goodsPropertyAdminMapper;
    @Resource
    private RedisService redisService;

    // 刷新商品菜单列表缓存
    public void updateGoodsMenuListRedisCache() {
        log.info("刷新商品菜单列表缓存");
        redisService.del(Const.CONST_goods_menu_vo_cache);
    }

    // TODO 重置所有商品的默认属性和默认价格
    @Transactional(rollbackFor = Exception.class)
    public void resetGoodsDefaultProperty() {
        log.info("重置所有商品的默认属性和默认价格");
        goodsPropertyAdminMapper.resetIsDefault(); // 全部重置
        for (GoodsDTO goods : goodsAdminMapper.selectList(null)) {
            // 重置默认价格和默认选择的大小
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_size.value, goods);
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_temperature.value, goods);
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_tian_du.value, goods);
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_kou_wei.value, goods);
        }
        updateGoodsMenuListRedisCache();
    }

    // 重置默认的必选属性
    private void resetGoodsDefaultPropertyHelper(String propertyCategory, GoodsDTO goods) {
        List<GoodsProperty> properties = goodsPropertyAdminMapper.selectList(
                new QueryWrapper<GoodsProperty>().eq("goods_id", goods.getId())
                        .eq("category", propertyCategory)
        );
        if (properties != null && properties.size() >= 1) {
            // 随便设置一个默认的属性
            GoodsProperty goodsProperty = properties.get(0);
            goodsProperty.setIsDefault(true);
            goodsPropertyAdminMapper.updateById(goodsProperty);

            if (GoodsPropertyCategory.ENUM_size.value.equalsIgnoreCase(propertyCategory)) {  // 大小属性就要重置商品的默认价格
                goods.setDefaultPrice(goodsProperty.getRebasePrice());
                goodsAdminMapper.updateById(goods);
            }
        }
    }

    // TODO 每天再次同步没有回调成功的订单交易号，有些已经付款了，但是回调失败了，主动去查
    //每天1点触发
    @Scheduled(cron = "0 0 1 * * ? ") //
    public void recheckWxPayTransactionId() {
        log.info("[timing task] 再次同步没有回调成功的订单交易号，有些已经付款了，但是回调失败了，主动去查");
        List<String> orderNoList = orderInfoAdminMapper.selectOrderNoByWxPayTransactionId(10);
        for (String orderNo : orderNoList) {
            try {
                orderInfoAdminService.queryWeixinOrder(orderNo);
            } catch (WxPayException e) {
                e.printStackTrace();
            }
        }

        // 删除所有未支付的订单(未支付或没有微信交易号)
        orderInfoAdminMapper.deleteOrderNotPay(OrderStatus.ENUM_has_not_pay_money.value);
    }

    // 每天确认已经完成的订单
    @Scheduled(cron = "0 0 2 * * ? ")
    public void confirmOrder() {
        log.info("[timing task] 每天确认已经完成的订单");
        OrderInfo order = new OrderInfo();
        order.setOrderStatus(OrderStatus.ENUM_has_completed.value);
        orderInfoAdminMapper.update(order,
                new QueryWrapper<OrderInfo>().eq("order_status", OrderStatus.ENUM_has_received.value));
    }
}
