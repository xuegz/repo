package com.jiancai.security;

import com.jiancai.common.BusinessException;

/**
 * 小程序用户接口鉴权：校验 Bearer token，并保证传入的 openid 与 token 一致。
 */
public final class MpAuth {

    private MpAuth() {
    }

    public static String requireOpenid(JwtUtil jwtUtil, String authorization, String claimed) {
        String token = authorization == null ? "" : authorization.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        String openid;
        try {
            openid = jwtUtil.parseUserOpenid(token);
        } catch (Exception e) {
            throw new BusinessException(401, "登录已过期，请重新进入小程序");
        }
        if (claimed != null && !claimed.isBlank() && !claimed.equals(openid)) {
            throw new BusinessException(403, "身份不匹配");
        }
        return openid;
    }
}
