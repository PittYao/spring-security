package com.fanyao.spring.security.config.authentication.logout;

import cn.hutool.db.dialect.impl.MysqlDialect;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.util.JwtTokenUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: bugProvider
 * @date: 2020/2/27 17:16
 * @description:
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("自定义登出处理器");
    }
}
