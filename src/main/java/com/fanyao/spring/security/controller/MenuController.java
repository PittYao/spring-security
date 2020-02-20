package com.fanyao.spring.security.controller;

import com.fanyao.spring.security.RspBean;
import com.fanyao.spring.security.model.po.Menu;
import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/17 15:41
 * @description:
 */
@Validated
@RestController
@RequestMapping("/menus")
public class MenuController {
    @Autowired
    private IMenuService menuService;

    @GetMapping("")
    public RspBean listMenuByLoginUser(Authentication authentication){
        // 获取登录用户信息
        String name = authentication.getName();
        User user = (User) authentication.getPrincipal();

        List<Menu> menus = menuService.listMenuByLoginUser();
        return RspBean.ok("登录用户菜单", menus);
    }
}
