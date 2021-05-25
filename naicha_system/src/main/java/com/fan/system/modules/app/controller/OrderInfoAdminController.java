package com.fan.system.modules.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.common.common.annotation.AccessLimiter;
import com.fan.common.common.annotation.NeedPermission;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.OrderInfo;
import com.fan.system.modules.app.service.OrderInfoAdminService;
import com.github.binarywang.wxpay.exception.WxPayException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "系统：订单管理")
@RestController
@RequestMapping("/orderInfoAdmin")
public class OrderInfoAdminController {

    @Resource
    private OrderInfoAdminService orderInfoAdminService;


    @ApiOperation("分页查询")
    @NeedPermission("system:app:orderInfo:list")
    @GetMapping("/page")
    public Result<Page<OrderInfo>> getOrderInfoAdminByPage(@RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestParam(required = false) String orderStatus) {
        return Result.ok(orderInfoAdminService.getOrderInfoAdminByPage(pageNo, pageSize, orderStatus));
    }


    @ApiOperation("进入订单的下一个步")
    @NeedPermission("system:app:orderInfo:update")
    @PostMapping("/nextStatus/{orderNo}/{currentStatus}")
    public Result<String> toNextOrderStatus(@PathVariable String orderNo, @PathVariable String currentStatus) throws ServiceException {
        return Result.ok(orderInfoAdminService.toNextOrderStatus(orderNo, currentStatus));
    }

    /*
        promotionDetail=null,
        deviceInfo=null,
        openid=oxci95SnSaCpN5NAvv66T4XYDVR8,
        isSubscribe=N,
        tradeType=JSAPI,
        tradeState=SUCCESS,
        bankType=OTHERS,
        totalFee=1,
        settlementTotalFee=null,
        feeType=CNY,
        cashFee=1,
        cashFeeType=CNY,
        couponFee=null,
        couponCount=null,
        coupons=null,
        transactionId=4200000993202104034676739322,
        outTradeNo=202104231628-1009,
        attach=,
        timeEnd=20210403231727,
        tradeStateDesc=支付成功
       * */
    @ApiOperation("主动查询微信订单的支付状态")
    @NeedPermission("system:app:orderInfo:status")
    @AccessLimiter
    @GetMapping("/wxPayStatus/{orderNo}")
    public Result<String> queryWeixinOrder(@PathVariable String orderNo) throws WxPayException {
        return Result.ok(orderInfoAdminService.queryWeixinOrder(orderNo).getTradeStateDesc());
    }

    @ApiOperation("商家取消没有付款的订单")
    @NeedPermission("system:app:orderInfo:cancel")
    @PutMapping("/cancelOrderNotPay/{orderNo}")
    public Result cancelOrderNotPay(@PathVariable String orderNo) throws ServiceException {
        return Result.ok(orderInfoAdminService.cancelOrderNotPay(orderNo));
    }

    @ApiOperation("商家取消订单并执行退款")
    @NeedPermission("system:app:orderInfo:refund")
    @PutMapping("/cancelAndRefund/{orderNo}")
    public Result cancelAndRefund(@PathVariable String orderNo, String reason) throws ServiceException {
        return Result.ok(orderInfoAdminService.cancelAndRefund(orderNo, reason));
    }
}
