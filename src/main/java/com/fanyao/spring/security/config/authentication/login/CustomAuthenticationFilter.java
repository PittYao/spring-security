package com.fanyao.spring.security.config.authentication.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author: bugProvider
 * @date: 2020/2/9 16:00
 * @description: 登录方式 结合为 表单登录 和 json登录
 */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // TODO 添加验证码功能 校验不通过抛出AuthenticationException()即可

        log.info("自定义json登录逻辑 CustomAuthenticationFilter");
        // json格式拦截
        if (request.getContentType().equals(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
            UsernamePasswordAuthenticationToken authRequest = null;
            ObjectMapper mapper = new ObjectMapper();
            try (InputStream in = request.getInputStream()) {
                Map authBean = mapper.readValue(in, Map.class);
                authRequest = new UsernamePasswordAuthenticationToken(
                        authBean.get("username"),
                        authBean.get("password")
                );
            } catch (IOException e) {
                e.printStackTrace();
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            } finally {
                assert authRequest != null;
                this.setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        } else {
            // 原有的
            return super.attemptAuthentication(request, response);
        }
    }
}
