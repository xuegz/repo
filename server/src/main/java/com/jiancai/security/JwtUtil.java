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
