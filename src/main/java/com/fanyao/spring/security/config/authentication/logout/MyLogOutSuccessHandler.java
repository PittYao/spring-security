package com.fanyao.spring.security.config.authentication.logout;

import com.fanyao.spring.security.RspBean;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: bugProvider
 * @date: 2020/2/12 12:19
 * @description: 登出成功处理器
 */
@NoArgsConstructor
@AllArgsConstructor
public class MyLogOutSuccessHandler implements LogoutSuccessHandler {
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
        String jwtToken = request.getHeader(JwtTokenUtil.TOKEN_HEADER);

        RspBean rspBean;
        if (Strings.isBlank(jwtToken)) {
            rspBean = RspBean.error("退出失败 | 未携带token");
        }else {
            // 删除redis access token
            String accessTokenRedisKey = JwtTokenUtil.getAccessTokenRedisKey(jwtToken);
            stringRedisTemplate.delete(accessTokenRedisKey);
            rspBean = RspBean.ok("登出成功");
        }

        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }
}
