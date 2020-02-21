package com.fanyao.spring.security.model.dto;

import com.fanyao.spring.security.model.validate.DefaultGroup;
import com.fanyao.spring.security.model.validate.OtherGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: bugProvider
 * @date: 2020/2/17 13:46
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    @NotNull(groups = OtherGroup.class, message = "id不能为空")
    private Integer id;

    @NotBlank(groups = DefaultGroup.class, message = "账号不能为空")
    private String userName;

    @NotBlank(groups = OtherGroup.class, message = "原密码不能为空")
    private String oldPassWord;

    @NotBlank(message = "密码不能为空")
    private String passWord;

}
