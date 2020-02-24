package com.fanyao.spring.security.config.authentication.login.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: bugProvider
 * @date: 2020/2/16 14:59
 * @description: 自定义token处理过滤器
 */
@Slf4j
public class MyTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("执行自定义过滤器 MyTokenFilter");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
