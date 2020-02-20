package com.fanyao.spring.security.config.authentication.login.handler;

import com.fanyao.spring.security.RspBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: bugProvider
 * @date: 2020/2/12 12:16
 * @description: 鉴权失败处理器
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        resp.setStatus(403);
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        RspBean rspBean = RspBean.error(403,"权限不足，访问失败");
        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }
}
