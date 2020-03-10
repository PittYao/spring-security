package com.fanyao.spring.security.config.authentication.login.filter;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fanyao.spring.security.RspBean;
import com.fanyao.spring.security.config.authentication.exception.MySecurityException;
import com.fanyao.spring.security.config.authentication.util.JwtTokenUtil;
import com.fanyao.spring.security.config.constants.MimeTypes;
import com.fanyao.spring.security.model.dto.UserDetailsDTO;
import com.fanyao.spring.security.model.po.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.ui.UIFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.dozer.Mapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: bugProvider
 * @date: 2020/2/24 09:40
 * @description: - 用户登录的过滤器，在用户的登录的过滤器中校验用户是否登录成功，如果登录成功，则生成一个token返回给客户端，登录失败则给前端一个登录失败的提示
 */
@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    private StringRedisTemplate stringRedisTemplate;
    private Mapper mapper;

    public JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, StringRedisTemplate stringRedisTemplate, Mapper mapper) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.stringRedisTemplate = stringRedisTemplate;
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse response) throws AuthenticationException {
        log.info("执行jwt 身份认证 过滤器 JwtLoginFilter");
        String contentType = req.getContentType();
        UsernamePasswordAuthenticationToken authenticationToken = null;
        String method = req.getMethod();

        if (RequestMethod.POST.toString().equals(method)) {
            if (MimeTypeUtils.APPLICATION_JSON_VALUE.equals(contentType)) {
                // json 登录
                ObjectMapper mapper = new ObjectMapper();
                try (InputStream in = req.getInputStream()) {
                    Map authBean = mapper.readValue(in, Map.class);
                    authenticationToken = new UsernamePasswordAuthenticationToken(
                            authBean.get(JwtTokenUtil.LOGIN_USERNAME),
                            authBean.get(JwtTokenUtil.LOGIN_PASSWORD)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                    authenticationToken = new UsernamePasswordAuthenticationToken("", "");
                }
            } else if (contentType.contains(MimeTypes.FORM_DATA) || MimeTypes.X_WWW_FORM_URLENCODED.equals(contentType)) {
                // 表单登录
                String username = req.getParameter(JwtTokenUtil.LOGIN_USERNAME);
                String password = req.getParameter(JwtTokenUtil.LOGIN_PASSWORD);
                authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            }
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } else {
            throw new MySecurityException("登录方式必须为POST");
        }
    }

    // 登录成功
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication authResult) throws IOException {
        // 构建所有角色名列表 用，连接
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        StringBuffer roleNames = new StringBuffer();
        if (CollectionUtils.isNotEmpty(authorities)) {
            for (GrantedAuthority authority : authorities) {
                roleNames.append(authority.getAuthority())
                        .append(",");
            }
        }

        User user = (User) authResult.getPrincipal();
        UserDetailsDTO userDetailsDTO = mapper.map(user, UserDetailsDTO.class);

        String userInfo = JSON.toJSONString(userDetailsDTO);

        // 创建token，放入用户信息
        String tokenId = IdUtil.fastSimpleUUID();

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtTokenUtil.AUTHORITIES, roleNames);
        claims.put(JwtTokenUtil.USER_ID, user.getId());
        claims.put(JwtTokenUtil.USER_INFO, userInfo); // 用户信息
        claims.put(JwtTokenUtil.TOKEN_ID, tokenId);

        // 验证码存入redis,并有过期时间: 用户id ---> 时间
        String accessTokenRedisKey = JwtTokenUtil.ACCESS_TOKEN + user.getId();
        String tokenValue = String.valueOf(DateUtil.currentSeconds()).concat("_").concat(tokenId);
        log.info("redis access_token 键: {} | 值: {}", accessTokenRedisKey, tokenValue);
        // ACCESS_TOKEN:userId:1 ===> 1583804094_tokenId

        stringRedisTemplate.opsForValue().set(accessTokenRedisKey, tokenValue, JwtTokenUtil.REDIS_EXPIRED, TimeUnit.SECONDS);

//        String jwt = JwtTokenUtil.createToken(null, authResult.getName(), 1800L, claims);
        // 创建永久token
        String jwt = JwtTokenUtil.createForeverToken(tokenId, null, authResult.getName(), claims);

        // 禁止前端缓存
        resp.setHeader("Access-Control-Expose-Headers", JwtTokenUtil.TOKEN_HEADER);
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader(JwtTokenUtil.TOKEN_HEADER, JwtTokenUtil.TOKEN_PREFIX + jwt);
        resp.setContentType("application/json;charset=utf-8");

        RspBean rspBean = RspBean.ok("登录成功");
        PrintWriter out = resp.getWriter();
        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }

    // 登录失败
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException {
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
            rspBean.setMsg("账户被禁用");
        } else if (e instanceof BadCredentialsException) {
            rspBean.setMsg("用户名或密码输入错误");
        } else if (e instanceof UsernameNotFoundException) {
            rspBean.setMsg("用户名不存在或用户未指定角色");
        } else if (e instanceof MySecurityException) {
            rspBean.setMsg(e.getMessage());
        } else {
            rspBean.setMsg("登录失败!");
        }
        out.write(new ObjectMapper().writeValueAsString(rspBean));
        out.flush();
        out.close();
    }
}
