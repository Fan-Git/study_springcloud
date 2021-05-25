package com.fan.common.common.config.tomcat;//package com.yu.common.config.tomcat;
//
//import org.apache.catalina.connector.Connector;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatConfig {
//    @Value("${my.properties.http.port}")
//    private int httpPort;
//
//    @Bean
//    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
//        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
//            @Override
//            public void customize(ConfigurableServletWebServerFactory factory) {
//                if (factory instanceof TomcatServletWebServerFactory) {
//                    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//                    // 设置http访问的端口号，不能与https端口重复，否则会报端口被占用的错误
//                    connector.setPort(httpPort);
//                    // 添加额外的tomcat连接器
//                    ((TomcatServletWebServerFactory) factory).addAdditionalTomcatConnectors(connector);
//                }
//            }
//        };
//    }
//}