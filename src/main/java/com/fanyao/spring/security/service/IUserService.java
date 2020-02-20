package com.fanyao.spring.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.model.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author: bugProvider
 * @date: 2020/2/14 14:33
 * @description:
 */
public interface IUserService extends UserDetailsService, IService<User> {
    User save(UserDTO userDTO);

    User updatePwd(UserDTO userDTO);
}
