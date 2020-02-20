package com.fanyao.spring.security.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:27
 * @description:
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class User implements Serializable, UserDetails {

    private static final long serialVersionUID = 100001L;

    /**
     * 序列号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 用户密码
     */
    @TableField("password")
    private String passWord;

    /**
     * 真实姓名
     */
    @TableField("username")
    private String userName;

    /**
     * 真实姓名
     */
    @TableField(value = "enabled",fill = FieldFill.INSERT)
    private Boolean enabled;

    // 账户是否没有过期
    @TableField(value = "account_non_expired", fill = FieldFill.INSERT)
    private Boolean accountNonExpired;

    // 账户是否没有被锁定
    @TableField(value = "account_non_locked", fill = FieldFill.INSERT)
    private Boolean accountNonLocked;

    // 密码是否没有过期
    @TableField(value = "credentials_non_expired", fill = FieldFill.INSERT)
    private Boolean credentialsNonExpired;

    @TableField(exist = false)
    private List<Role> roles;

    // 账户拥有的角色
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        }
        return authorities;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return passWord;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }
}