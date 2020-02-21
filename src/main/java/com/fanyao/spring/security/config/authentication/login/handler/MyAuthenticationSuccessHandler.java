package com.fanyao.spring.security.config.authentication.login.handler;

import com.fanyao.spring.security.RspBean;
import com.fanyao.spring.security.model.dto.UserDetailsDTO;
import com.fanyao.spring.security.model.po.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: bugProvider
 * @date: 2020/2/12 11:47
 * @description: 验证成功处理器
 */
@NoArgsConstructor
@AllArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private Mapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();

        // 返回用户包装
        User user = (User) authentication.getPrincipal();
        UserDetailsDTO userDetailsDTO = mapper.map(user, UserDetailsDTO.class);

        RspBean rspBean = RspBean.ok("登录成功", userDetailsDTO);
        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }
}
