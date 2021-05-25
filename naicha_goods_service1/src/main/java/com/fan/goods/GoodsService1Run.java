package com.fan.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication(scanBasePackages = {"com.fan"})
@MapperScan("com.fan.**.mapper")
@RestController
@EnableDiscoveryClient    // 用于发现eureka 注册中心的微服务。
@EnableEurekaClient       // 可以作为数据微服务客户端
public class GoodsService1Run {
    public static void main(String[] args) {
        SpringApplication.run(GoodsService1Run.class, args);
    }

    @GetMapping({"/status", "/"})
    public String run(HttpServletRequest request) {
        return "后台服务器正常运行 [system_general]";
    }

    @GetMapping({"/404"})
    public String _404() {
        return "不存在对应资源 [system]";
    }
}
