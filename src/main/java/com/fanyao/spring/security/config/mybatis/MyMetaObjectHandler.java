package com.fanyao.spring.security.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: bugProvider
 * @date: 2019/10/10 13:22
 * @description: 数据库字段自动填充
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.setInsertFieldValByName("createTime", new Date(),metaObject);
        this.setInsertFieldValByName("modifyTime", new Date(), metaObject);
        this.setInsertFieldValByName("deleted", 0, metaObject);
        this.setInsertFieldValByName("status", 0, metaObject);
        this.setInsertFieldValByName("accountNonExpired", 1, metaObject);
        this.setInsertFieldValByName("accountNonLocked", 1, metaObject);
        this.setInsertFieldValByName("credentialsNonExpired", 1, metaObject);
        this.setInsertFieldValByName("enabled", 1, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setUpdateFieldValByName("modifyTime", new Date(),metaObject);
    }
}
