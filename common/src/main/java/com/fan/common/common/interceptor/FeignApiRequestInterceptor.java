package com.fan.common.common.interceptor;


import com.fan.common.common.constant.Const;
import com.fan.common.common.util.servlet.ServletUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class FeignApiRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = request.getHeader(Const.CONST_token);
        if (!StringUtils.isEmpty(token)) {
            requestTemplate.header(Const.CONST_token, request.getHeader(Const.CONST_token));
        } else {
            requestTemplate.query(Const.CONST_token, request.getParameter(Const.CONST_token));
        }
    }
}