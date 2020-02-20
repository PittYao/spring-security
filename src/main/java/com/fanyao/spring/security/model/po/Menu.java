package com.fanyao.spring.security.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Menu implements Serializable {

    private static final long serialVersionUID = 100002L;

    /**
     * 序列号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("url")
    private String url;

    @TableField("path")
    private String path;

    @TableField("component")
    private String component;

    @TableField("name")
    private String name;

    @TableField("iconCls")
    private String iconCls;

    @TableField("keepAlive")
    private Boolean keepAlive;

    @TableField("requireAuth")
    private Boolean requireAuth;

    @TableId(value = "parentId")
    private Integer parentId;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("isMenu")
    private Boolean isMenu;

    @TableField(exist = false)
    private List<Role> roles;

    @TableField(exist = false)
    private List<Menu> children;

}