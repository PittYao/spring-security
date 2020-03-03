package com.fanyao.spring.security.config;

import com.fanyao.spring.security.config.authentication.auth.MyAuthenticationEntryPoint;
import com.fanyao.spring.security.config.authentication.auth.UrlAccessDecisionManager;
import com.fanyao.spring.security.config.authentication.auth.UrlFilterInvocationSecurityMetadataSource;
import com.fanyao.spring.security.config.authentication.login.CustomAuthenticationFilter;
import com.fanyao.spring.security.config.authentication.login.filter.MyTokenFilter;
import com.fanyao.spring.security.config.authentication.login.filter.ValidateCodeFilter;
import com.fanyao.spring.security.config.authentication.login.handler.MyAccessDeniedHandler;
import com.fanyao.spring.security.config.authentication.login.handler.MyAuthenticationFailureHandler;
import com.fanyao.spring.security.config.authentication.login.handler.MyAuthenticationSuccessHandler;
import com.fanyao.spring.security.config.authentication.login.provider.MyAuthenticationProvider;
import com.fanyao.spring.security.config.authentication.login.voter.MyExpressionVoter;
import com.fanyao.spring.security.config.authentication.logout.MyLogOutSuccessHandler;
import com.fanyao.spring.security.config.authentication.white.SecurityWhiteList;
import com.fanyao.spring.security.service.IMenuService;
import com.fanyao.spring.security.service.IUserService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
    private SecurityWhiteList securityWhiteList;
    @Autowired
    private Mapper mapper;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    public static final String loginProcessesUrl = "/login";

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()//开启登录配置
//                .antMatchers("/hello").hasRole("admin")//表示访问 /hello 这个接口，需要具备 admin 这个角色
//                .antMatchers("/hello1").hasRole("user")//表示访问 /hello1 这个接口，需要具备 user 这个角色
                .anyRequest().authenticated()//表示剩余的其他接口 都需要认证
                .withObjectPostProcessor(getObjectPostProcessor())// 鉴权一
//                .accessDecisionManager(accessDecisionManager())// 鉴权二
                .and().formLogin()

//                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
//                .loginPage("/login")
//                //登录处理接口
//                .loginProcessingUrl("/sign_in")
//                //定义登录时，用户名的 key，默认为 username
//                .usernameParameter("username")
//                //定义登录时，用户密码的 key，默认为 password
//                .passwordParameter("password")
//                //登录成功的处理器
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
//                        resp.setContentType("application/json;charset=utf-8");
//                        PrintWriter out = resp.getWriter();
//                        out.write("success");
//                        out.flush();
//                        out.close();
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
//                        resp.setContentType("application/json;charset=utf-8");
//                        PrintWriter out = resp.getWriter();
//                        out.write("fail");
//                        out.flush();
//                        out.close();
//                    }
//                })

                .permitAll()//和登录相关的接口统统都直接通过
                .and().httpBasic()
                .and().csrf().disable();


        // 在LogoutFilter后加入自定义过滤器
        http.addFilterAfter(new MyTokenFilter(), LogoutFilter.class);

        // 添加验证码校验过滤器
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class);

        // 登录
        http.addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 登出
        http.logout().logoutUrl("/logout").logoutSuccessHandler(new MyLogOutSuccessHandler());

        // 异常处理
        http.exceptionHandling()
                // 权限不足
                .accessDeniedHandler(new MyAccessDeniedHandler())
                // 未登录直接访问接口 返回异常json
                .authenticationEntryPoint(new MyAuthenticationEntryPoint());


    }

    // 鉴权
    private ObjectPostProcessor<FilterSecurityInterceptor> getObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                // 注册 获取路径权限是否满足的两个过滤器
                o.setSecurityMetadataSource(new UrlFilterInvocationSecurityMetadataSource(menuService));
                o.setAccessDecisionManager(new UrlAccessDecisionManager(securityWhiteList));
                return o;
            }
        };
    }

    /**
     * 自定义json登录/login过滤器
     */
    private CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();

        filter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler(mapper));
        filter.setAuthenticationFailureHandler(new MyAuthenticationFailureHandler());
        // 登录地址
        filter.setFilterProcessesUrl(loginProcessesUrl);
        // authenticationManagerBean默认的认证逻辑
//        filter.setAuthenticationManager(authenticationManagerBean());
        // 多方式登录组合
        filter.setAuthenticationManager(getAuthenticationManager());
        return filter;
    }

    // -------------------------登录认证逻辑 开始------------------------------//
    // 自定义的认证逻辑
    private MyAuthenticationProvider getMyAuthenticationProvider() {
        return new MyAuthenticationProvider(userService, passwordEncoder);
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
