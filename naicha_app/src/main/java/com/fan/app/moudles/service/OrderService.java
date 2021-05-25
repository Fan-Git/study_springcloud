package com.fan.app.moudles.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.fan.common.common.util.result.Result;
import com.fan.common.entity.app.form.CreateOrderForm;
import com.fan.common.entity.app.vo.HistoryOrderVO;
import com.fan.common.common.exception.ServiceException;
import com.fan.common.entity.app.OrderInfo;

import java.util.List;

public interface OrderService {

    // 创建订单
    Result<String> createOrder(CreateOrderForm orderParams, String wxOpenid) throws ServiceException;

    // 微信小程序预先支付
    Object wxPrepay(String wxOpenid, String orderNo, String payIp) throws ServiceException;

    // 分页获取历史订单
    Page<HistoryOrderVO> getHistoryOrderByPage(Integer pageNo, Integer pageSize, String wxOpenid) throws ServiceException;

    // 获取订单详情
    OrderInfo getOrderDetail(String orderNo) throws ServiceException;

    // 取消订单
    Integer cancelOrder(String orderNo) throws WxPayException;

    // 完成订单
    Integer finishedOrder(String orderNo, String wxOpenid) throws ServiceException;

    // 处理中订单
    List<OrderInfo> getHandlingOrders(String wxOpenid) throws ServiceException;
}
