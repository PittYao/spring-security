package com.fanyao.spring.security.config.authentication.auth;

import com.fanyao.spring.security.model.po.Menu;
import com.fanyao.spring.security.model.po.Role;
import com.fanyao.spring.security.service.IMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
//    @Autowired
    private IMenuService menuService;
    private List<Menu> allMenu;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public UrlFilterInvocationSecurityMetadataSource(IMenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //获取请求地址
        String requestUrl = ((FilterInvocation) object).getRequestUrl();

        log.info("通过当前的请求地址，获取该地址需要的用户角色名称集合");

        if (CollectionUtils.isEmpty(allMenu)) {
            loadResourcePermission();
        }

        for (Menu menu : allMenu) {
            if (antPathMatcher.match(menu.getUrl(), requestUrl) && !CollectionUtils.isEmpty(menu.getRoles())) {
                // 构建 角色名 集合
                List<String> roleNames = menu.getRoles().stream().map(Role::getName).collect(Collectors.toList());

                String[] values = new String[roleNames.size()];
                roleNames.toArray(values);

                return SecurityConfig.createList(values);
            }
        }
        //没有匹配上的资源，都是登录访问
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
     * 加载资源权限
     */
//    @PostConstruct
    private void loadResourcePermission() {
        allMenu = menuService.listAll();
    }
}
