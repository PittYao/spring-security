package com.fanyao.spring.security.config.authentication.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * @author: bugProvider
 * @date: 2020/2/21 09:49
 * @description:
 */
public class MySecurityException extends AccountStatusException {

    public MySecurityException(String msg) {
        super(msg);
    }

    public MySecurityException(String msg, Throwable t) {
        super(msg, t);
    }
}
