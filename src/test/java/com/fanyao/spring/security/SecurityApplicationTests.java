package com.fanyao.spring.security;

import com.fanyao.spring.security.dao.MenuMapper;
import com.fanyao.spring.security.dao.UserMapper;
import com.fanyao.spring.security.model.po.Menu;
import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.service.IMenuService;
import com.fanyao.spring.security.service.IRoleService;
import com.fanyao.spring.security.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Slf4j
@SpringBootTest
class SecurityApplicationTests {
    @Autowired
    private IMenuService menuService;
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IRoleService roleService;

    @Test
    void contextLoads() {
        System.out.println(new BCryptPasswordEncoder().encode("666"));
        // $2a$10$m4qmAMJsrGboHlGI/P2N6O5sLtjbkhVCEZRHd0BV26cXi4nU0E6Be
    }

    @Test
    void test() {
        List<Menu> menus = menuService.listAll();
        log.info(menus.toString());
    }

    @Test
    void testLoadUser() {
        List<User> admin = userMapper.selectOneIncludeRoles("admin");
    }

    @Test
    void testMenu() {
        Menu menu = menuMapper.getMenuAndChildrenById(6);
        log.info(menu.toString());
    }

    @Test
    void testRole() {
        List<Integer> integers = roleService.listMenuIdByRoleId(6, 1);
        log.info(integers.toString());
    }
}
