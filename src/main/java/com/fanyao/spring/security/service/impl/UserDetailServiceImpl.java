package com.fanyao.spring.security.service.impl;

import com.fanyao.spring.security.service.IUserDetailService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author: bugProvider
 * @date: 2020/2/21 10:15
 * @description:
 */
public class UserDetailServiceImpl implements IUserDetailService {

    /** 
     * 根据用户登录名定位用户
     * @param username	用户名
     * @return 用户
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
