package com.fan.common.common.util.weixin;

import com.alibaba.fastjson.JSON;
import com.fan.common.common.util.http.HttpUtil;
import com.fan.common.common.config.property.WeixinProperty;
import com.fan.common.common.util.spring.SpringContextUtil;
import lombok.Data;

import java.text.SimpleDateFormat;

public class WeixinUtil {
    private static final WeixinProperty weixinProperty = SpringContextUtil.getBeanByClass(WeixinProperty.class);
//    /**
//     * 统一服务消息
//     * 小程序模板消息,发送服务通知
//     *
//     * @param token      小程序ACCESS_TOKEN
//     * @param touser     用户openid，可以是小程序的openid，也可以是公众号的openid
//     * @param templateId 小程序模板消息模板id
//     * @param page       小程序页面路径
//     * @param jsonData    小程序模板消息
//     * @return
//     * @ emphasis_keyword 小程序模板放大关键词
//     * @author HGL
//     */
//    public static String sendWeappMessage(String token, String touser, String templateId,String formId
//                                          String page, JSONObject jsonData) {
//        try {
//            JSONObject obj = new JSONObject();
//            String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=" + token;
//            obj.put("touser", touser);
//            JSONObject weapp_template_msg = new JSONObject();
//            weapp_template_msg.put("template_id", templateId);
//            weapp_template_msg.put("page", page);
//            weapp_template_msg.put("data", jsonData);
//            // weapp_template_msg.put("emphasis_keyword", data.getJSONObject("keyword1").getString("value"));
//            obj.put("weapp_template_msg", weapp_template_msg);
//            return HttpUtil.postJson(url, obj.toJSONString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }


    @Data
    public static class TemplateData {
        String value;
        String color;
    }

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 发送公众号通知的方式，发送取餐消息通知用户
     */
    public static void sendMpMessageOfFinishOrder(String openId, String orderNo, String goodsInfo) {
        return ;
//        String accessToken = getAccessTokenForSendMessage(); // 先获取accessToken
//        JSONObject obj = new JSONObject();
//        JSONObject mp_template_msg = new JSONObject();
//        JSONObject miniprogram = new JSONObject();
//        try {
//            String path = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=" + accessToken;
//            obj.put("touser", openId);
//            mp_template_msg.put("appid", weixinProperty.getMpAppid());
//            mp_template_msg.put("template_id", weixinProperty.getMessageTemplateId());
//            mp_template_msg.put("url", "pages/mine/mine");  // 公众号模板消息所要跳转的url
//            miniprogram.put("appid", weixinProperty.getAppid()); // 公众号模板消息所要跳转的小程序appid，小程序的必须与公众号具有绑定关系
//            miniprogram.put("page", "pages/mine/mine"); // 公众号模板消息所要跳转的小程序页面
//            mp_template_msg.put("miniprogram", miniprogram);
//            Map<String, TemplateData> mapdata = new LinkedHashMap<>();
//
//            // 封装模板数据
//            TemplateData orderNoData = new TemplateData();
//            orderNoData.setValue(orderNo);
//            orderNoData.setColor("#173177");
//            mapdata.put("xxxxxxxxkey1要改", orderNoData);
//
//            TemplateData goodsInfoData = new TemplateData();
//            goodsInfoData.setValue(goodsInfo);
//            goodsInfoData.setColor("#173177");
//            mapdata.put("xxxxxxxxkey1要改", goodsInfoData);
//
//            TemplateData createTimeData = new TemplateData();
//            createTimeData.setValue(simpleDateFormat.format(new Date()));
//            createTimeData.setColor("#173177");
//            mapdata.put("xxxxxxxxkey1要改", createTimeData);
//
//            mp_template_msg.put("data", mapdata);
//            obj.put("mp_template_msg", mp_template_msg);
//            return HttpUtil.postJson(path, obj.toJSONString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "发送消息失败";
    }

    // 获取发送消息需要的accessToken
    public static String getAccessTokenForSendMessage() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + weixinProperty.getAppid() + "&secret=" + weixinProperty.getAppSecret();
        return JSON.parseObject(HttpUtil.getHtml(url)).getString("access_token");
    }

}
