package com.fanyao.spring.security.config.authentication.login.handler;

import com.fanyao.spring.security.RspBean;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.exception.ValidateCodeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: bugProvider
 * @date: 2020/2/12 11:47
 * @description: 登陆失败处理器
 */
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
        resp.setContentType("application/json;charset=utf-8");

        PrintWriter out = resp.getWriter();
        RspBean rspBean = RspBean.error("登录失败");

        if (e instanceof LockedException) {
            rspBean.setMsg("账户被锁定");
        } else if (e instanceof CredentialsExpiredException) {
            rspBean.setMsg("密码过期");
        } else if (e instanceof AccountExpiredException) {
            rspBean.setMsg("账户过期");
        } else if (e instanceof DisabledException) {
            rspBean.setMsg("账户被禁用过期");
        } else if (e instanceof BadCredentialsException) {
            rspBean.setMsg("用户名或密码输入错误");
        } else if (e instanceof UsernameNotFoundException) {
            rspBean.setMsg("用户名不存在或用户未指定角色");
        } else if (e instanceof MySecurityException || e instanceof ValidateCodeException) {
            rspBean.setMsg(e.getMessage());
        } else {
            rspBean.setMsg("登录失败!");
        }

        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }
}
