package com.fanyao.spring.security.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/11 13:27
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role implements Serializable {

    private static final long serialVersionUID = 100002L;

    /**
     * 序列号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 角色名
     */
    @TableField("name")
    private String name;

    /**
     * 角色中文名
     */
    @TableField("nameZh")
    private String nameZh;

    @TableField(exist = false)
    private List<Menu> menus;

}