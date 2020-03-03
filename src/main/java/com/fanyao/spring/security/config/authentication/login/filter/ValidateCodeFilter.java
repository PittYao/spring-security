package com.fanyao.spring.security.config.authentication.login.filter;

import cn.hutool.core.date.DateUtil;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.image.ImageCode;
import com.fanyao.spring.security.config.authentication.login.handler.MyAuthenticationFailureHandler;
import com.fanyao.spring.security.controller.ValidateController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author: bugProvider
 * @date: 2020/3/3 10:48
 * @description: 验证码验证器
 */
@Slf4j
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {
    private AuthenticationFailureHandler authenticationFailureHandler = new MyAuthenticationFailureHandler();

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // url = login && method = post
        if (StringUtils.equalsIgnoreCase("/login", httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), HttpMethod.POST.name())) {
            try {
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (MySecurityException e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // 验证逻辑
    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
        String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");

        log.info("时间:{} 请求验证码 ===> {} 用户输入验证码 ===> {}", DateUtil.now(), codeInSession.getCode(), codeInRequest);

        if (StringUtils.isBlank(codeInRequest)) {
            throw new MySecurityException("验证码不能为空!");
        }

        if (Objects.isNull(codeInSession)) {
            throw new MySecurityException("验证码不存在!");
        }

        if (codeInSession.isExpire()) {
            sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
            throw new MySecurityException("验证码已过期!");
        }

        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
            throw new MySecurityException("验证码不正确!");
        }

        sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
    }
}
