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
 * @author 25380
 */
@Configuration
public class FeignConfiguration {
    @Bean
    public Logger.Level feignLog(){
        return Logger.Level.FULL;
    }
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignConfiguration(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    public Encoder multipartFormEncoder() {
        // 此编码器专门用于处理multipart/form-data格式
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
}
