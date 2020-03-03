package com.fanyao.spring.security.config.authentication.white;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/3/3 10:18
 * @description: 请求白名单
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityWhiteList {
    private List<String> whiteList;
}
