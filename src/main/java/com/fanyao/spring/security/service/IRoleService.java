package com.fanyao.spring.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanyao.spring.security.model.po.Role;

import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/14 14:33
 * @description:
 */
public interface IRoleService extends IService<Role> {
    List<Integer> listMenuIdByRoleId(Integer id,Integer parentId);

    List<Role> listRoleByUserId(Integer userId);
}
