package com.fanyao.spring.security.config.authentication.util;

/**
 * @author: bugProvider
 * @date: 2020/2/23 17:50
 * @description:
 */

import io.jsonwebtoken.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.*;

/**
 * <p>JwtTokenUtil</p>
 */
public class JwtTokenUtil {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASSWORD = "password";
    public static final String AUTHORITIES = "authorities";
    public static final String SIGNATURE_EXCEPTION = "SignatureException";
    public static final String EXPIREDJWT_EXCEPTION = "ExpiredJwtException";

    /**
     * 密钥
     */
    private static final String SECRET = "jwt_secret_bug_provider";
    private static final String ISS = "bug_provider"; //签发者

    /**
     * 过期时间是 1800 秒 半小时
     */
    private static final long EXPIRATION = 1800L;

    public static String createToken(String issuer, String subject, long expiration) {
        return createToken(issuer, subject, expiration, null);
    }

    /**
     * 创建 token
     *
     * @param issuer     签发人
     * @param subject    主体,即用户信息的JSON
     * @param expiration 有效时间(秒)
     * @param claims     自定义参数
     * @return
     * @description todo https://www.cnblogs.com/wangshouchang/p/9551748.html
     */
    public static String createToken(String issuer, String subject, long expiration, Map<String, Object> claims) {
        return Jwts.builder()
                // JWT_ID：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
//                .setId(id)
                // 签名算法以及密匙
                .signWith(SignatureAlgorithm.HS512, SECRET)
                // 自定义属性
                .setClaims(Optional.ofNullable(claims).orElse(null))
                // 主题：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                // 受众
//                .setAudience(loginName)
                // 签发人
                .setIssuer(Optional.ofNullable(issuer).orElse(ISS))
                // 签发时间
                .setIssuedAt(new Date())
                // 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + (expiration > 0 ? expiration : EXPIRATION) * 1000))
                .compact();
    }

    /**
     * 从 token 中获取主题信息
     *
     * @param token
     * @return
     */
    public static String getProperties(String token) {
        return getTokenBody(token).getSubject();
    }


    /**
     * 校验是否过期
     *
     * @param token
     * @return
     */
    public static boolean isExpiration(String token) throws SignatureException, ExpiredJwtException {
        if (token.contains(JwtTokenUtil.TOKEN_PREFIX)) {
            token = token.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        }
        return getTokenBody(token).getExpiration().before(new Date());
    }

    /**
     * 获得 token 的 body
     *
     * @param token
     * @return
     */
    private static Claims getTokenBody(String token) throws SignatureException, ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    // 这里从token中获取用户信息并新建一个token
    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (token.contains(JwtTokenUtil.TOKEN_PREFIX)) {
            token = token.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        }
        Claims claims = JwtTokenUtil.getTokenBody(token);

        // 获取当前登录用户名
        String username = claims.getSubject();
        if (Strings.isEmpty(username)) {
            return new UsernamePasswordAuthenticationToken("", "", new ArrayList<>());
        }

        // 用户角色
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get(JwtTokenUtil.AUTHORITIES));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

}

