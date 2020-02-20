package com.fanyao.spring.security.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanyao.spring.security.config.authentication.util.IAuthenticationFacade;
import com.fanyao.spring.security.dao.MenuMapper;
import com.fanyao.spring.security.model.po.Menu;
import com.fanyao.spring.security.model.po.Role;
import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.service.IMenuService;
import com.fanyao.spring.security.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:39
 * @description:
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private IRoleService roleService;
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Override
    public List<Menu> listAll() {
        return menuMapper.listAll();
    }

    @Override
    public List<Menu> listMenuByLoginUser() {
        // 登录用户所有的角色 来 获取所有菜单资源
        User user = authenticationFacade.getLoginUser();
        assert user != null;

        // 所有角色
        List<Role> roles = user.getRoles();

        if (CollectionUtils.isEmpty(roles)) {
            throw new RuntimeException("该用户没有绑定角色");
        }

        Set<Integer> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());


        List<Integer> allMenuIds = new ArrayList<>();
        // 获取角色所有 菜单
        roleIds.forEach(id -> {
            List<Integer> menuIds = roleService.listMenuIdByRoleId(id,1);
            if (CollectionUtils.isNotEmpty(menuIds)) {
                allMenuIds.addAll(menuIds);
            }
        });

        if (CollectionUtils.isEmpty(allMenuIds)) {
            throw new RuntimeException("该用户角色没有绑定菜单");
        }

        List<Menu> menus = new ArrayList<>();
        allMenuIds.forEach(menuId ->{
            Menu menu = menuMapper.getMenuAndChildrenById(menuId);
            if (Objects.nonNull(menu)) {
                menus.add(menu);
            }
        });

        return menus;
    }
}
