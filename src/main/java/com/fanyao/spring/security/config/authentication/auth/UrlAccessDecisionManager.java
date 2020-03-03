package com.fanyao.spring.security.config.authentication.auth;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fanyao.spring.security.config.authentication.white.SecurityWhiteList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: bugProvider
 * @date: 2020/2/16 20:30
 * @description: 自定义AccessDecisionManager
 * -    UrlFilterInvocationSecurityMetadataSource 返回名称后到 AccessDecisionManager
 */
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    private SecurityWhiteList securityWhiteList;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public UrlAccessDecisionManager(SecurityWhiteList securityWhiteList) {
        this.securityWhiteList = securityWhiteList;
    }

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, AuthenticationException {
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        List<String> whiteList = securityWhiteList.getWhiteList();
        // collection 本次需要的角色
        for (ConfigAttribute ca : collection) {
            //当前请求需要的权限
            String needRole = ca.getAttribute();
            if ("ROLE_LOGIN".equals(needRole)) {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    // 未登录 访问白名单 也可放行
                    if (CollectionUtils.isNotEmpty(whiteList)) {
                        for (String whiteUrl : whiteList) {
                            if (antPathMatcher.match(whiteUrl, requestUrl)) {
                                return;
                            }
                        }
                    } else {
                        // 不是白名单 既提示未登录
                        throw new BadCredentialsException("未登录");
                    }
                } else {
                    // ROLE_LOGIN则表示登录即可访问，和角色没有关系
                    return;
                }
            }

            //当前用户所具有的权限
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (CollectionUtils.isNotEmpty(authorities)) {
                for (GrantedAuthority authority : authorities) {
                    // 有其中一个角色 既满足权限 TODO 也可要求必须满足所有角色才放行
                    if (authority.getAuthority().equals(needRole)) {
                        return;
                    }
                }
            }
        }
        throw new AccessDeniedException("权限不足!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
