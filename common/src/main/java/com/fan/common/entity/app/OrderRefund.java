package com.fan.common.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("订单退款")
public class OrderRefund {
    public enum OrderRefundStatus {
        ENUM_success("SUCCESS", "退款成功"),
        ENUM_closed("CLOSED", "退款已关闭"),
        ENUM_processing("PROCESSING", "退款处理中"),
        ENUM_abnormal("ABNORMAL", "退款失败");

        public String status;
        public String message;

        OrderRefundStatus(String status, String text) {
            this.status = status;
            this.message = text;
        }

        @Override
        public String toString() {
            return this.status;
        }
    }

    @TableId(type = IdType.INPUT)
    private String refundNo; // 商户退款号
    private String wxRefundId; // 微信支付退款号
    private String wxPayTransactionId; // 微信支付订单号
    private String orderNo; // 商户订单号
    private Date createTime;
    private Date successTime;
    private String status; // 退款状态: 成功，关闭，处理中，异常 | SUCCESS','CLOSED','PROCESSING','ABNORMAL'
    private Integer refundFee;
    private Integer orderPayPrice; // 订单交易金额

}
