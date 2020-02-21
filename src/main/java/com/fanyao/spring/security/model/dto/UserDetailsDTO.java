package com.fanyao.spring.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: bugProvider
 * @date: 2020/2/21 10:33
 * @description:
 */
@Data
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

}
