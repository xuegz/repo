package com.jiancai.security;

import com.jiancai.common.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败限流：同一账号连续失败 5 次锁定 10 分钟（单实例内存计数，重启后清零）
 */
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_MILLIS = 10 * 60 * 1000L;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    /** 登录前校验是否处于锁定期 */
    public void check(String username) {
        Attempt attempt = attempts.get(username);
        if (attempt == null) {
            return;
        }
        synchronized (attempt) {
            long now = System.currentTimeMillis();
            if (attempt.lockUntil > now) {
                long remainMinutes = (attempt.lockUntil - now + 59_999) / 60_000;
                throw new BusinessException("密码错误次数过多，请 " + remainMinutes + " 分钟后再试");
            }
            if (attempt.lockUntil > 0) {
                attempts.remove(username, attempt);
            }
        }
    }

    /** 记录一次失败，返回剩余可尝试次数；返回 0 表示已锁定 */
    public int recordFailure(String username) {
        Attempt attempt = attempts.computeIfAbsent(username, key -> new Attempt());
        synchronized (attempt) {
            long now = System.currentTimeMillis();
            if (attempt.lockUntil > now) {
                return 0;
            }
            attempt.failures++;
            if (attempt.failures >= MAX_ATTEMPTS) {
                attempt.lockUntil = now + LOCK_MILLIS;
                attempt.failures = 0;
                return 0;
            }
            return MAX_ATTEMPTS - attempt.failures;
        }
    }

    /** 登录成功清除失败记录 */
    public void clear(String username) {
        attempts.remove(username);
    }

    private static class Attempt {
        private int failures;
        private long lockUntil;
    }
}
