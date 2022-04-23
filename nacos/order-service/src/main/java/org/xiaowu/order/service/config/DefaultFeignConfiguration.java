package org.xiaowu.order.service.config;

import feign.Feign;
import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration  {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.BASIC; // 日志级别为BASIC
    }
}