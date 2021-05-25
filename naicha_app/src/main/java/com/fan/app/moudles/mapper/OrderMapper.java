package com.fan.app.moudles.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fan.common.entity.app.vo.HistoryOrderVO;
import com.fan.common.entity.app.OrderInfo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface OrderMapper extends BaseMapper<OrderInfo> {

    // 微信支付成功的再次回调，填入交易号、支付时间(yyyyMMddHHmmss格式)、再次更新订单状态、要避免重复调用
    @Update("UPDATE order_info SET order_status=#{param2}, wx_pay_transaction_id=#{param3}, pay_price=#{param4}, pay_time=#{param5}  " +
            " WHERE order_no=#{param1} and wx_pay_transaction_id is null; ")
    int finishPayOrderByWxPayNotify(String orderNo, String orderStatus, String wxPayTransactionId, Integer payPrice, String payTime);

    // 分页获取历史订单
    @Select("select order_no as orderNo, " +
            "order_status as orderStatus, " +
            "goods_preview as goodsPreview, " +
            "goods_total_num as goodsTotalNum, " +
            "create_time as createTime, " +
            "pay_price as payPrice " +
            "from order_info where wx_openid = #{param1} order by create_time desc limit #{param2},#{param3};")
    List<HistoryOrderVO> getHistoryOrderByPage(String wxOpenid, Integer rowStart, Integer pageSize);

    // 分页获取历史订单, 获取总数
    @Select("select count(*) from order_info where wx_openid = #{param1}; ")
    int getHistoryOrderTotalCount(String wxOpenid);

    // 小程序完成支付
    @Update("update order_info set order_status = #{param2}, pay_time=#{param3} where order_no = #{param1}")
    int finishPayOrder(String orderNo, String orderStatus, Date payTime);

    // 完成订单
    @Update("update order_info set order_status = #{param2}, finish_time=#{param3} where order_no = #{param1}")
    int finishOrder(String orderNo, String orderStatus, Date finishTime);

    // 订单的总价格
    @Select("select total_price from order_info where order_no = #{param1}; ")
    Integer getOrderTotalPriceByOrderNo(String orderNo);
}
