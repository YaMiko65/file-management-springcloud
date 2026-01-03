package com.test.common_api.config;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign客户端配置类
 * 用于配置Feign客户端的相关功能，如日志级别和表单编码器
 */
@Configuration
public class FeignConfiguration {
    /**
     * 配置Feign客户端日志级别
     * 设置为FULL级别，记录完整的请求和响应信息
     *
     * @return Feign日志级别
     */
    @Bean
    public Logger.Level feignLog(){
        return Logger.Level.FULL;
    }
    /**
     * HTTP消息转换器工厂
     */
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    /**
     * 构造函数
     *
     * @param messageConverters HTTP消息转换器工厂
     */
    public FeignConfiguration(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 配置多部分表单编码器
     * 用于处理multipart/form-data格式的数据，如文件上传
     *
     * @return 多部分表单编码器
     */
    @Bean
    public Encoder multipartFormEncoder() {
        // 此编码器专门用于处理multipart/form-data格式
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
}
