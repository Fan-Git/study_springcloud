package com.fan.common.common.config.property;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 微信相关属性
@Data
@Configuration
public class WeixinProperty {
    //wx85b896927c383237
    private String appid = "wx6b19a997a7114a13";
    private String appSecret = "20c8b8b1c356b99e14ef3ce2de108da0";
    private String mchId = "1607726716"; // 个人商户号
    private String mchKey = "lifei000000000000000000000000000";
    private String notifyUrl = "https://192.168.0.107:9001/api-app/order/orderNotifyUrl"; // 微信支付回调地址
    private String keyPath = ""; // apiv3的证书地址 如: 直接放项目下，退款时要用

    //    private String apiSecret = "lifei000000000000000000000000000"; // 秘钥
    //    private String apiv3Secret = "lifei000000000000000000000000000"; // 商户必须先设置好apiv3秘钥后才能解密回调通知，防止报文被他人恶意篡改。
    private String mpAppid = ""; // 公众号的appid
    /*
          取餐编号
          {{character_string12.DATA}}
          餐品详情
          {{thing11.DATA}}
           点餐时间
          {{date3.DATA}}
     */
    private String messageTemplateId = ""; // 公众号的订单消息模板
}
