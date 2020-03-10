package com.fanyao.spring.security.config;

import com.fanyao.spring.security.config.authentication.auth.JwtAuthenticationFilter;
import com.fanyao.spring.security.config.authentication.auth.MyAuthenticationEntryPoint;
import com.fanyao.spring.security.config.authentication.auth.UrlAccessDecisionManager;
import com.fanyao.spring.security.config.authentication.auth.UrlFilterInvocationSecurityMetadataSource;
import com.fanyao.spring.security.config.authentication.login.filter.JwtLoginFilter;
import com.fanyao.spring.security.config.authentication.login.filter.MyTokenFilter;
import com.fanyao.spring.security.config.authentication.login.handler.MyAccessDeniedHandler;
import com.fanyao.spring.security.config.authentication.login.provider.MyAuthenticationProvider;
import com.fanyao.spring.security.config.authentication.login.voter.MyExpressionVoter;
import com.fanyao.spring.security.config.authentication.logout.CustomLogoutHandler;
import com.fanyao.spring.security.config.authentication.logout.MyLogOutSuccessHandler;
import com.fanyao.spring.security.service.IMenuService;
import com.fanyao.spring.security.service.IUserService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: bugProvider
 * @date: 2020/2/9 12:30
 * @description: -   所有接口被保护
 * -   需先访问/login接口 json格式
 * -   再访问其他接口
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Mapper mapper;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 身份认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 注册一个账号
//        auth.inMemoryAuthentication()
//                .withUser("admin").password(passwordEncoder().encode("666")).roles("admin")
//                .and()
//                .withUser("user").password(passwordEncoder().encode("666")).roles("user");
        // 单方式登录验证
//        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/resources/**/*.html", "/resources/**/*.js");
        // 不验证的url
        web.ignoring().antMatchers("/pubic");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // 开启登录配置
                .withObjectPostProcessor(getObjectPostProcessor())  // 鉴权
                .anyRequest().authenticated()//表示剩余的其他接口，登录之后就能访问
                .and()
                .formLogin()
                .permitAll()
                .and().httpBasic()
                .and().csrf().disable();

        // 在LogoutFilter后加入自定义过滤器
        http.addFilterAfter(new MyTokenFilter(), LogoutFilter.class);

        // 禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 登录
        http.addFilterBefore(new JwtLoginFilter("/login", getAuthenticationManager(), stringRedisTemplate, mapper), UsernamePasswordAuthenticationFilter.class);

        // 校验token
        http.addFilterBefore(new JwtAuthenticationFilter(stringRedisTemplate), UsernamePasswordAuthenticationFilter.class);

        // 登出
        http.logout()
                .logoutUrl("/logout")
                .addLogoutHandler(new CustomLogoutHandler(stringRedisTemplate))
                .logoutSuccessHandler(new MyLogOutSuccessHandler(stringRedisTemplate))
                .clearAuthentication(true);

        // 统一异常处理
        http.exceptionHandling()
                // 权限不足
                .accessDeniedHandler(new MyAccessDeniedHandler())
                // 不登录直接访问接口 返回异常json
                .authenticationEntryPoint(new MyAuthenticationEntryPoint());

    }

    // 鉴权
    private ObjectPostProcessor<FilterSecurityInterceptor> getObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                // 注册 获取路径权限是否满足的两个过滤器
                o.setSecurityMetadataSource(new UrlFilterInvocationSecurityMetadataSource(menuService));
                o.setAccessDecisionManager(new UrlAccessDecisionManager());
                return o;
            }
        };
    }

    // -------------------------登录认证逻辑 开始------------------------------//
    // 自定义的认证逻辑
    private MyAuthenticationProvider getMyAuthenticationProvider() {
        return new MyAuthenticationProvider(userService, passwordEncoder, new AccountStatusUserDetailsChecker());
    }

    // 默认的认证逻辑
    private DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    // 组合认证逻辑集合
    private AuthenticationManager getAuthenticationManager() {
        // 注意顺序
        return new ProviderManager(
//                Arrays.asList(
//                        getMyAuthenticationProvider(),
//                        daoAuthenticationProvider())

                Collections.singletonList(
                        getMyAuthenticationProvider())
        );
    }

    // -------------------------登录认证逻辑 结束------------------------------//

    // -------------------------鉴权策略 开始------------------------------//
    private AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters
                = Arrays.asList(
                new MyExpressionVoter(),
                new WebExpressionVoter(),
                new RoleVoter(),
                new AuthenticatedVoter());
        return new UnanimousBased(decisionVoters);
    }
    // -------------------------鉴权策略 结束------------------------------//


}
