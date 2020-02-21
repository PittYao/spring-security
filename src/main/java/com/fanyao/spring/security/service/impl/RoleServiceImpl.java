package com.fanyao.spring.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanyao.spring.security.dao.RoleMapper;
import com.fanyao.spring.security.model.po.Role;
import com.fanyao.spring.security.service.IRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:39
 * @description:
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMapper roleMapper;


    @Override
    public List<Integer> listMenuIdByRoleId(Integer id,Integer parentId) {
        return roleMapper.listMenuIdByIdAndParentId(id,parentId);
    }

    @Override
    public List<Role> listRoleByUserId(Integer userId) {
        return roleMapper.listRoleByUserId(userId);
    }
}
