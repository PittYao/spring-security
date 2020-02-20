package com.fanyao.spring.security.config.authentication.role;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

/**
 * @author: bugProvider
 * @date: 2020/2/11 10:32
 * @description: 角色继承
 */
@Component
public class MyRoleHierarchy {

    // admin 具有 user 所有权限
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_admin > ROLE_user \n";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
