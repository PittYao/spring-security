package com.fanyao.spring.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanyao.spring.security.model.po.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:36
 * @description:
 */
public interface UserMapper extends BaseMapper<User> {
    User selectOneIncludeRoles(@Param("userName") String userName);
}
