package com.fanyao.spring.security.controller;

import com.fanyao.spring.security.RspBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bugProvider
 * @date: 2020/2/9 12:30
 * @description:
 */
@RestController
public class LoginController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/hello1")
    public String hello1() {
        return "hello1";
    }

    @GetMapping("/login")
    public RspBean login() {
        return RspBean.error("未登录，请登录");
    }

    @GetMapping("/public")
    public String testPublic() {
        return "public";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("session/invalid")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String sessionInvalid(){
        return "session已失效，请重新认证";
    }
}
