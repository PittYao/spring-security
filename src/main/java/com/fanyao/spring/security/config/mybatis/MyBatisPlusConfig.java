package com.fanyao.spring.security.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * @author: bugProvider
 * @date: 2019/10/9 17:31
 * @description: MyBatisPlus配置
 */

@Configuration
@MapperScan("com.fanyao.spring.security.dao")
public class MyBatisPlusConfig {

    // 分页
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }

    // TODO 设置 dev test 环境开启 显示sql计时和执行语句 生产环境要关闭 消耗性能
    @Bean
    @Profile({"dev","test"})
    public PerformanceInterceptor performanceInterceptor(){
        return new PerformanceInterceptor();
    }
}
