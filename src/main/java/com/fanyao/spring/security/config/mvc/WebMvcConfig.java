package com.fanyao.spring.security.config.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author: bugProvider
 * @date: 2020/3/2 17:12
 * @description:
 */
//@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 配置静态资源
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/html/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}
