package com.jiancai.security;

import com.jiancai.common.BusinessException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录与权限拦截器：解析 JWT，并按 URL 前缀校验角色
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            throw new BusinessException(401, "未登录");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        LoginUser user;
        try {
            user = jwtUtil.parseToUser(token);
        } catch (JwtException e) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
        if (user.getUserId() == null) {
            throw new BusinessException(401, "登录信息不完整");
        }

        // 角色校验：/api/admin/** 仅管理员，/api/merchant/** 商户或管理员
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/admin") && !Integer.valueOf(1).equals(user.getRole())) {
            throw new BusinessException(403, "无权限访问平台管理接口");
        }

        UserContext.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
