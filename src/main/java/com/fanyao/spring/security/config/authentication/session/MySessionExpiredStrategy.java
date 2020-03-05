package com.fanyao.spring.security.config.authentication.session;

import com.fanyao.spring.security.RspBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: bugProvider
 * @date: 2020/3/3 14:02
 * @description: session策略组
 */
@Component
public class MySessionExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletResponse response = event.getResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");
        RspBean rspBean = RspBean.ok("您的账号已经在别的地方登录，当前登录已失效。如果密码遭到泄露，请立即修改密码！");
        response.getWriter().write(new ObjectMapper().writeValueAsString(rspBean));
    }
}
