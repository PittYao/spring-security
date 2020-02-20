package com.fanyao.spring.security.config.authentication.util;

import com.fanyao.spring.security.model.po.User;
import org.springframework.security.core.Authentication;

/**
 * @author: bugProvider
 * @date: 2020/2/17 16:05
 * @description: security工具
 */
public interface IAuthenticationFacade {
    // 获取当前登录用户
    Authentication getAuthentication();

    User getLoginUser();
}
