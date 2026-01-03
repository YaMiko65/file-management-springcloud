/**
 * 操作日志服务启动类
 * 启动操作日志微服务，提供操作日志记录、查询和管理功能
 * 配置了MyBatis-Plus的Mapper扫描和Feign客户端支持
 */
package com.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.test.mapper")
public class LogApplication {
    /**
     * 启动操作日志服务应用程序
     * 使用SpringApplication.run方法启动Spring Boot应用程序
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }
}
