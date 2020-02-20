package com.fanyao.spring.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanyao.spring.security.model.po.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:36
 * @description:
 */
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> listAll();

    Menu getMenuAndChildrenById(@Param("id") Integer id);
}
