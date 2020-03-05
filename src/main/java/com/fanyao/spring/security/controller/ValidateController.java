package com.fanyao.spring.security.controller;

import com.fanyao.spring.security.config.authentication.image.ImageCode;
import com.fanyao.spring.security.config.authentication.image.ImageCodeRedisDTO;
import com.fanyao.spring.security.config.authentication.util.ImageCodeUtil;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: bugProvider
 * @date: 2020/3/3 10:35
 * @description: 图形验证码
 */
@RestController
public class ValidateController {

    public final static String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = ImageCodeUtil.createImageCode();
        // 验证码存储到session中
        ImageCodeRedisDTO imageCodeRedisDTO = imageCode.convert2RedisDTO();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_IMAGE_CODE, imageCodeRedisDTO);
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }
}
