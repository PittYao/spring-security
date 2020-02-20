package com.fanyao.spring.security.controller;

import com.fanyao.spring.security.model.po.User;
import com.fanyao.spring.security.model.dto.UserDTO;
import com.fanyao.spring.security.model.validate.DefaultGroup;
import com.fanyao.spring.security.model.validate.OtherGroup;
import com.fanyao.spring.security.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

/**
 * @author: bugProvider
 * @date: 2020/2/17 13:44
 * @description:
 */
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("")
    public User save(@RequestBody @Validated({DefaultGroup.class, Default.class}) UserDTO userDTO) {
        return userService.save(userDTO);
    }

    @PutMapping("")
    public User updatePwd(@RequestBody @Validated({OtherGroup.class, Default.class})  UserDTO userDTO) {
        return userService.updatePwd(userDTO);
    }
}
