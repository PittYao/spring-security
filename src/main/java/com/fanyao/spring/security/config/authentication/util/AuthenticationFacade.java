package com.fanyao.spring.security.config.authentication.util;

import com.fanyao.spring.security.model.po.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author: bugProvider
 * @date: 2020/2/17 16:06
 * @description:
 */
@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public User getLoginUser() {
        return (User) getAuthentication().getPrincipal();
    }
}
