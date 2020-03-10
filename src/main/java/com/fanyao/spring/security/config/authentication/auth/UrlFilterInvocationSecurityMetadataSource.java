package com.fanyao.spring.security.config.authentication.auth;

import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.util.JwtTokenUtil;
import com.fanyao.spring.security.model.po.Menu;
import com.fanyao.spring.security.model.po.Role;
import com.fanyao.spring.security.service.IMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: bugProvider
 * @date: 2020/2/16 16:33
 * @description: 通过当前的请求地址，获取该地址需要的用户角色名称集合
 */
@Slf4j
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private IMenuService menuService;
    private List<Menu> allMenu;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public UrlFilterInvocationSecurityMetadataSource(IMenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 校验token是否有异常
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String details = (String) authentication.getDetails();


        if (JwtTokenUtil.SIGNATURE_EXCEPTION.equals(details)) {
            throw new MySecurityException("token校验失败 | 不是有效token");
        } else if (JwtTokenUtil.EXPIRED_JWT_EXCEPTION.equals(details)) {
            throw new MySecurityException("token已过期 | 请重新登录");
        }


        FilterInvocation filterInvocation = (FilterInvocation) object;
        //获取请求地址
        String requestUrl = filterInvocation.getRequestUrl();

        log.info("通过当前的请求地址 ===> {}，获取该地址需要的用户角色名称集合", requestUrl);

        // FIXME 只加载了一次全部资源,修改数据库后,只能重启服务,才会生效,redis加载菜单资源
        if (CollectionUtils.isEmpty(allMenu)) {
            loadResourcePermission();
        }

        for (Menu menu : allMenu) {
            // FIXME url匹配 精准匹配未排序
            if (antPathMatcher.match(menu.getUrl(), requestUrl) && !CollectionUtils.isEmpty(menu.getRoles())) {
                // 构建 角色名 集合
                List<String> roleNames = menu.getRoles().stream().map(Role::getName).collect(Collectors.toList());

                String[] values = new String[roleNames.size()];
                roleNames.toArray(values);

                return SecurityConfig.createList(values);
            }
        }

        //没有匹配上的资源，但是登录了即可访问
        return SecurityConfig.createList("ROLE_LOGIN");
        // return null; 当前这个请求不需要任何角色就能访问，甚至不需要登录
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 加载全部资源
     */
    private void loadResourcePermission() {
        allMenu = menuService.listAll();
    }
}
