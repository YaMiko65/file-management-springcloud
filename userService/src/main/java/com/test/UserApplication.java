/**
 * 用户服务启动类
 * 启动用户管理微服务，提供用户登录、注册、信息管理等功能
 * 配置了MyBatis-Plus的Mapper扫描和Feign客户端支持
 */
package com.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.test.mapper")
@EnableFeignClients
public class UserApplication {
    /**
     * 启动用户服务应用程序
     * 使用SpringApplication.run方法启动Spring Boot应用程序
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
