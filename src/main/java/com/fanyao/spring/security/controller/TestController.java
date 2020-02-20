package com.fanyao.spring.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bugProvider
 * @date: 2020/2/9 12:30
 * @description:
 */
@RestController
@RequestMapping("/tests")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "tests/test";
    }
}
