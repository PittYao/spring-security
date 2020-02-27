package com.fanyao.spring.security.config.authentication.util;

/**
 * @author: bugProvider
 * @date: 2020/2/23 17:50
 * @description:
 */

import com.alibaba.fastjson.JSON;
import com.fanyao.spring.security.model.dto.UserDetailsDTO;
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

    //---------login----------------//
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASSWORD = "password";
    //---------login----------------//

    //---------token------------------//
    public static final String AUTHORITIES = "authorities";
    public static final String USER_ID = "userId";
    public static final String USER_INFO = "userInfo";
    //    public static final Integer REDIS_EXPIRED = 7 * 24 * 60 * 60; // 7天
    public static final Integer REDIS_EXPIRED = 60 * 60; // 1天
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN:userId:";
    //---------token------------------//

    //--------Exception---------------//
    public static final String SIGNATURE_EXCEPTION = "SignatureException";
    public static final String EXPIRED_JWT_EXCEPTION = "ExpiredJwtException";
    //--------Exception---------------//

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
     * @description
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
     * 创建 永久token
     *
     * @param issuer  签发人
     * @param subject 主体,即用户信息的JSON
     * @param claims  自定义参数
     * @return
     * @description
     */
    public static String createForeverToken(String issuer, String subject, Map<String, Object> claims) {
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
                .setExpiration(null)
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
        Date expiration = getTokenBody(token).getExpiration();
        if (expiration != null) {
            return expiration.before(new Date());
        } else {
            return false;
        }
    }

    /**
     * 获得 token 的 body
     *
     * @param token
     * @return
     */
    public static Claims getTokenBody(String token) throws SignatureException, ExpiredJwtException {
        if (token.contains(JwtTokenUtil.TOKEN_PREFIX)) {
            token = token.replace(JwtTokenUtil.TOKEN_PREFIX, "");
        }
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

        String userInfo = (String) claims.get(JwtTokenUtil.USER_INFO);
        UserDetailsDTO userDetailsDTO = JSON.parseObject(userInfo, UserDetailsDTO.class);

        // TODO username
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsDTO, null, authorities);
        authenticationToken.setDetails("");
        return authenticationToken;
    }

    // token中获取redis key
    public static String getAccessTokenRedisKey(String jwtToken) {
        Claims claims = getTokenBody(jwtToken);
        Object o = claims.get(JwtTokenUtil.USER_ID);

        return JwtTokenUtil.ACCESS_TOKEN + o.toString();
    }

}

