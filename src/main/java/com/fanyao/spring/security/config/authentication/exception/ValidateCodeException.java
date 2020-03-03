package com.fanyao.spring.security.config.authentication.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: bugProvider
 * @date: 2020/3/3 10:52
 * @description: 验证码异常
 */
public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = 5022575393500654458L;

    public ValidateCodeException(String message) {
        super(message);
    }
}