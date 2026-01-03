/**
 * 用户界面服务启动类
 * 启动用户界面微服务，提供Web界面和用户交互功能
 * 配置了Feign客户端支持以与其他微服务通信
 */
package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UiApplication {
    /**
     * 启动用户界面服务应用程序
     * 使用SpringApplication.run方法启动Spring Boot应用程序
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }
}
