package com.jiancai.security;

/**
 * 当前登录用户上下文（ThreadLocal）
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> LOCAL = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        LOCAL.set(user);
    }

    public static LoginUser get() {
        return LOCAL.get();
    }

    public static Long getUserId() {
        LoginUser u = LOCAL.get();
        return u == null ? null : u.getUserId();
    }

    public static Integer getRole() {
        LoginUser u = LOCAL.get();
        return u == null ? null : u.getRole();
    }

    public static Long getMerchantId() {
        LoginUser u = LOCAL.get();
        return u == null ? null : u.getMerchantId();
    }

    public static void clear() {
        LOCAL.remove();
    }
}
