package com.fanyao.spring.security.config.authentication.login.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author: bugProvider
 * @date: 2020/2/16 14:59
 * @description: 自定义token处理过滤器
 */
@Slf4j
public class MyTokenFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        log.info("执行自定义过滤器 MyTokenFilter");
        filterChain.doFilter(request, response);
    }
}
