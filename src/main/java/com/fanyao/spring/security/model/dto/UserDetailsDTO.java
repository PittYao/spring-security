package com.fanyao.spring.security.model.dto;

import com.fanyao.spring.security.model.po.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/21 10:33
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO implements Serializable {
    private static final long serialVersionUID = 10000000000001L;
    /**
     * 序列号
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    private List<Role> roles;

}
