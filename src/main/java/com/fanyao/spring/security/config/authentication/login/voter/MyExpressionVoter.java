package com.fanyao.spring.security.config.authentication.login.voter;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Collection;

/**
 * @author: bugProvider
 * @date: 2020/2/16 14:03
 * @description: 自定义 投票器
 */
public class MyExpressionVoter extends WebExpressionVoter {

    @Override
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
        // 鉴权逻辑 1=通过 0=弃权 -1=反对 投票管理器收集投票vote结果，如果最终结果大于等于0则放行该请求。
        return super.vote(authentication, fi, attributes);
    }
}
