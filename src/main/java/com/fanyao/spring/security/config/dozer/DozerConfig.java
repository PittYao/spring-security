package com.fanyao.spring.security.config.dozer;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author: bugProvider
 * @date: 2019/10/15 09:42
 * @description:
 */
@Component
public class DozerConfig {

    @Bean
    public Mapper getMapper() {
        return new DozerBeanMapper();
    }
}
