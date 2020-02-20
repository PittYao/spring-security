package com.fanyao.spring.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanyao.spring.security.model.po.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:36
 * @description:
 */
public interface RoleMapper extends BaseMapper<Role> {
    List<Integer> listMenuIdByIdAndParentId(@Param("id") Integer id, @Param("parentId") Integer parentId);
}
