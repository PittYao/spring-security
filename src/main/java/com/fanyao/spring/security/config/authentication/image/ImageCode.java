package com.fanyao.spring.security.config.authentication.image;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * @author: bugProvider
 * @date: 2020/3/2 16:41
 * @description: 图形验证码
 */
@Data
public class ImageCode  {
    // 图片
    private BufferedImage image;
    // 验证码
    private String code;
    // 过期时间
    private LocalDateTime expireTime;

    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime) {
        this.image = image;
        this.code = code;
        this.expireTime = expireTime;
    }

    public ImageCodeRedisDTO convert2RedisDTO(){
        return ImageCodeRedisDTO.builder()
                .code(this.code)
                .expireTime(this.expireTime)
                .build();
    }
}
