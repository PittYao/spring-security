package com.fanyao.spring.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.util.IAuthenticationFacade;
import com.fanyao.spring.security.dao.UserMapper;
import com.fanyao.spring.security.model.dto.UserDTO;
import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
    private IAuthenticationFacade authenticationFacade;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        assert username != null;
        List<User> users = userMapper.selectOneIncludeRoles(username);

        if (CollectionUtils.isEmpty(users)) {
            throw new UsernameNotFoundException("用户名不存在或用户未指定角色");
        }

        if (users.size() > 1) {
            throw new MySecurityException("用户名已存在并有角色");
        }

        return users.get(0);
    }

    @Override
    public User save(UserDTO userDTO) {

        List<User> users = this.list(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDTO.getUserName()));

        if (CollectionUtils.isNotEmpty(users)) {
            throw new RuntimeException("账号已存在");
        }

        // 密码加密
        String encodePwd = passwordEncoder.encode(userDTO.getPassWord());
        userDTO.setPassWord(encodePwd);

        // TODO
        User user = User.builder()
                .username(userDTO.getUserName())
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

        User user = this.getById(userDTO.getId());

        if (Objects.isNull(user)) {
            throw new RuntimeException("修改用户不存在");
        }

        boolean matches = passwordEncoder.matches(userDTO.getOldPassWord(), user.getPassword());
        if (!matches) {
            throw new RuntimeException("原密码不正确");
        }

        boolean same = passwordEncoder.matches(userDTO.getPassWord(), user.getPassword());
        if (same) {
            throw new RuntimeException("新密码和原密码相同");
        }

        String encodePwd = passwordEncoder.encode(userDTO.getPassWord());
        user.setPassWord(encodePwd);

        this.updateById(user);

        return user;
    }
}
