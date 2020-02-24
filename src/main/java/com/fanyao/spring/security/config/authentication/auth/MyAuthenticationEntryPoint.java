package com.fanyao.spring.security.config.authentication.auth;

import com.fanyao.spring.security.RspBean;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: bugProvider
 * @date: 2020/2/12 12:18
 * @description: 异常处理 直接访问接口 拒绝信息
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException) throws IOException, ServletException {
        resp.setStatus(401);
        resp.setContentType("application/json;charset=utf-8");
        String errorMsg = "访问失败,需先登录";

        if (authException instanceof MySecurityException) {
            errorMsg = authException.getMessage();
        }

        RspBean rspBean = RspBean.error(401, errorMsg);

        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }
}
