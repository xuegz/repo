package com.jiancai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    @Value("${jiancai.jwt.secret}")
    private String secret;

    @Value("${jiancai.jwt.expire-hours}")
    private long expireHours;

    @Value("${jiancai.jwt.user-expire-hours:720}")
    private long userExpireHours;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token */
    public String generateToken(Long userId, Integer role, Long merchantId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("role", role)
                .claim("merchantId", merchantId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireHours * 3600_000))
                .signWith(getKey())
                .compact();
    }

    /** 小程序用户 token：只承载 openid，用于聊天接口与长连接 */
    public String generateUserToken(String openid) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(openid)
                .claim("type", "mp")
                .claim("openid", openid)
                .issuedAt(new Date(now))
                .expiration(new Date(now + userExpireHours * 3600_000))
                .signWith(getKey())
                .compact();
    }

    /** 校验小程序用户 token 并返回 openid */
    public String parseUserOpenid(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("缺少登录凭证");
        }
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(getKey()).build()
                    .parseSignedClaims(token.trim()).getPayload();
        } catch (JwtException e) {
            throw new JwtException("登录已过期");
        }
        if (!"mp".equals(claims.get("type"))) {
            throw new JwtException("凭证类型不正确");
        }
        Object openid = claims.get("openid");
        if (openid == null || openid.toString().isBlank()) {
            throw new JwtException("凭证无效");
        }
        return openid.toString();
    }

    /** 解析 token，失败时抛 JwtException */
    public LoginUser parseToUser(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(getKey()).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            throw new JwtException("token 无效或已过期");
        }
        LoginUser user = new LoginUser();
        user.setUserId(toLong(claims.get("userId")));
        user.setRole(toInt(claims.get("role")));
        user.setMerchantId(toLong(claims.get("merchantId")));
        return user;
    }

    private Long toLong(Object val) {
        return val == null ? null : Long.valueOf(val.toString());
    }

    private Integer toInt(Object val) {
        return val == null ? null : Integer.valueOf(val.toString());
    }
}
