package com.fanyao.spring.security.config.authentication.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: bugProvider
 * @date: 2020/2/21 09:49
 * @description:
 */
public class MySecurityException extends AuthenticationException {

    public MySecurityException(String msg) {
        super(msg);
    }

    public MySecurityException(String msg, Throwable t) {
        super(msg, t);
    }
}
