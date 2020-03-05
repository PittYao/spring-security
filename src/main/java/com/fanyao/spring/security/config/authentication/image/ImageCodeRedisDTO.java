package com.fanyao.spring.security.config.authentication.image;

import lombok.Builder;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: bugProvider
 * @date: 2020/3/2 16:41
 * @description: 图形验证码 redis存储
 */
@Data
@Builder
public class ImageCodeRedisDTO implements Serializable {
    // 验证码
    private String code;
    // 过期时间
    private LocalDateTime expireTime;

    public ImageCodeRedisDTO(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ImageCodeRedisDTO(String code, LocalDateTime expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    // 判断验证码是否已过期
    public boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }

}
