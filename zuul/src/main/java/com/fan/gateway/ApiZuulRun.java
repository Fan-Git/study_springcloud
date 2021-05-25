package com.fan.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@RestController
public class ApiZuulRun {
    public static void main(String[] args) {
        SpringApplication.run(ApiZuulRun.class, args);

    }
    @GetMapping("/status")
    public String status(){
        return "zuul网关运行正常";
    }

    @GetMapping("/404")
    public String _404(){
        return "无法找到对应资源 [网关]";
    }
}
