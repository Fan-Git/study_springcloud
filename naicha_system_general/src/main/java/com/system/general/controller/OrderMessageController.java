package com.system.general.controller;

import com.fan.common.common.annotation.AccessLimiter;
import com.fan.common.common.util.result.Result;
import com.fan.common.service.OrderMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "系统：消息服务")
@RestController
@RequestMapping("/message")
public class OrderMessageController {

    @Resource
    private OrderMessageService orderMessageService;

    @ApiOperation("查询最新的订单消息")
    @AccessLimiter
    @GetMapping("/resentOrderMessage")
    public Result getResentOrderMessages() {
        return Result.ok(orderMessageService.getResentOrderMessages());
    }

    @ApiOperation("确认收到消息")
    @DeleteMapping("/confirmReceiveOrderMessage/{orderNo}")
    public Result<String> confirmReceiveOrderMessage(@PathVariable String orderNo) {
        return Result.ok(orderMessageService.confirmReceiveOrderMessage(orderNo));
    }

}
