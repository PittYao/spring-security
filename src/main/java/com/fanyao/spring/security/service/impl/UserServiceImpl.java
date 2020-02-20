package com.fanyao.spring.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanyao.spring.security.config.authentication.util.IAuthenticationFacade;
import com.fanyao.spring.security.dao.UserMapper;
import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.model.dto.UserDTO;
import com.fanyao.spring.security.service.IUserService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:39
 * @description:
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Mapper mapper;
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        assert username != null;
        User user = userMapper.selectOneIncludeRoles(username);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户名不存在或用户未指定角色");
        }

        return user;
    }

    @Override
    public User save(UserDTO userDTO) {
        // 账号已存在
        if (userMapper.selectOneIncludeRoles(userDTO.getUserName()) != null) {
            throw new RuntimeException("账号已存在");
        }

        String encodePwd = passwordEncoder.encode(userDTO.getPassWord());
        userDTO.setPassWord(encodePwd);

        // TODO
        User user = User.builder()
                .userName(userDTO.getUserName())
                .passWord(userDTO.getPassWord())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        this.save(user);

        return user;
    }

    @Override
    public User updatePwd(UserDTO userDTO) {
        Integer loginUserId = authenticationFacade.getLoginUser().getId();

        if (loginUserId == null) {
            throw new RuntimeException("登录用户不存在");
        }

        User user = this.getById(loginUserId);

        boolean matches = passwordEncoder.matches(userDTO.getOldPassWord(), user.getPassword());
        if (!matches) {
            throw new RuntimeException("原密码不正确");
        }

        String encodePwd = passwordEncoder.encode(userDTO.getPassWord());
        user.setPassWord(encodePwd);

        this.updateById(user);

        return user;
    }
}
