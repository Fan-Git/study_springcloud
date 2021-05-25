package com.fan.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class MyZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 过滤器在什么环境下执行
        return FilterConstants.PRE_TYPE; // 转发之前执行
    }

    @Override
    public int filterOrder() {
        return 1; //过滤器的执行顺序
    }

    @Override
    public boolean shouldFilter() { // 是否需要过滤
        return true;
    }

    // 拦截器具体执行
    @Override
    public Object run() {
        // 获取requestContext容器
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        System.out.println(request.getLocalPort()+"网关拦截器: " + request.getServletPath());
//        if(request.getServletPath().startsWith("/api-view/products")) {
//            log.error("网关拦截器，拦截未登录，拦截路径：" + request.getServletPath());
//            currentContext.setResponseStatusCode(HttpStatus.HTTP_UNAUTHORIZED);
//            currentContext.setSendZuulResponse(false);
//            return null;
//        }
//            //通过request方式获取inputStream  不知道参数名，用流获取请求体的数据
//            requestData = StreamUtils.copyToString(request.getInputStream(), Charsets.UTF_8);
//            currentContext.addZuulRequestHeader("Content-Type",
//                    MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        return null;
    }
}
