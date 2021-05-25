package com.fan.gateway.filter;

import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletResponse;

// 错误处理
@Component
public class MyErrorFilter extends SendErrorFilter {
    @Override
    public Object run() {
        String msg = "请求失败！";
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            ExceptionHolder exception = findZuulException(ctx.getThrowable());
            System.out.println("错误信息:" + exception.getErrorCause());
            msg += "error:" + exception.getErrorCause();
            HttpServletResponse response = ctx.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(msg.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return msg;
    }
}