package com.fanyao.spring.security.config.authentication.auth;

import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author: bugProvider
 * @date: 2020/2/24 12:17
 * @description: 鉴定token有效性 当其他请求发送来，校验token的过滤器，如果校验成功，就让请求继续执行
 *  FIXME 未能有效捕获Filter异常,暂时new UsernamePasswordAuthenticationToken替代
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication;
        String token_exception = "";

        String jwtToken = httpServletRequest.getHeader(JwtTokenUtil.TOKEN_HEADER);

        if (Strings.isEmpty(jwtToken)) {
            throw new MySecurityException("访问未带token,请登录");
        }

        try {
            JwtTokenUtil.isExpiration(jwtToken);
        } catch (SignatureException e) {
            log.error("token校验失败 | token签名不正确");
            e.printStackTrace();
            token_exception = JwtTokenUtil.SIGNATURE_EXCEPTION;
        } catch (ExpiredJwtException e) {
            //  FIXME 过期后 续签 或者 指定重新登录
            log.error("token校验失败 | token已过期 | token信息 ===> {}", e.getClaims().toString());
            e.printStackTrace();
            token_exception = JwtTokenUtil.EXPIREDJWT_EXCEPTION;
        } finally {
            authentication = new UsernamePasswordAuthenticationToken(token_exception, "", new ArrayList<>());
        }

        // 填充用户信息
        if (Strings.isBlank(token_exception)) {
            authentication = JwtTokenUtil.getAuthentication(jwtToken);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
