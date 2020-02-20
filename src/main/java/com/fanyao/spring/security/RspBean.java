package com.fanyao.spring.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author: bugProvider
 * @date: 2020/2/9 15:21
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RspBean {
    private Integer code;
    private String msg;
    private Object data;

    public static RspBean error(String msg){
        return RspBean.builder().code(500).msg(msg).build();
    }

    public static RspBean error(Integer code,String msg){
        return RspBean.builder().code(code).msg(msg).build();
    }

    public static RspBean ok(String msg){
        return RspBean.builder().code(200).msg(msg).build();
    }

    public static RspBean ok(String msg, Object data){
        return RspBean.builder().code(200).msg(msg).data(data).build();
    }
}

