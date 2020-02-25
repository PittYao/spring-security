package com.fanyao.spring.security.config.authentication.login.provider;

import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.service.IUserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * @author: bugProvider
 * @date: 2020/2/12 12:00
 * @description: -    验证器
 * -    自定义的登录验证逻辑
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class MyAuthenticationProvider implements AuthenticationProvider {
    private IUserService userService;
    private PasswordEncoder bCryptPasswordEncoder;
    private UserDetailsChecker userDetailsChecker;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 认证逻辑
        log.info("登录验证中 MyAuthenticationProvider");
        String userName = (String) authentication.getPrincipal();
        String passWord = (String) authentication.getCredentials();

        if (Objects.isNull(userName) || Objects.isNull(passWord)) {
            throw new MySecurityException("用户名或密码不能为空");
        }

        UserDetails userDetails = userService.loadUserByUsername(userName);

        // check 账号是否被锁定
        userDetailsChecker.check(userDetails);

        // 验证密码
        boolean matches = bCryptPasswordEncoder.matches(passWord, userDetails.getPassword());

        if (matches) {
            return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }

        // 验证不通过 进入下一个provider
        throw new BadCredentialsException("用户名或密码输入错误");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 启用
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
