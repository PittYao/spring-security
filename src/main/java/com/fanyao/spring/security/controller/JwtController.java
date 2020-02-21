package com.fanyao.spring.security.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: bugProvider
 * @date: 2020/2/20 23:20
 * @description:
 */
public class JwtController {
    @GetMapping("/jwt")
    public String jwt() {
        return "jwt";
    }
}
