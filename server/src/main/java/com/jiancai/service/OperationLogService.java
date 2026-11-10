package com.jiancai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.entity.OperationLog;
import com.jiancai.entity.SysUser;
import com.jiancai.mapper.OperationLogMapper;
import com.jiancai.mapper.SysUserMapper;
import com.jiancai.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;

/**
 * 管理端操作日志
 */
@Slf4j
@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {

    private static final int MAX_DETAIL_LENGTH = 500;

    @Autowired
    private SysUserMapper sysUserMapper;

    public void record(String action, String targetType, Long targetId, String detail) {
        try {
            OperationLog entry = new OperationLog();
            Long userId = UserContext.getUserId();
            entry.setUserId(userId);
            if (userId != null) {
                SysUser user = sysUserMapper.selectById(userId);
                if (user != null) {
                    entry.setUsername(user.getUsername());
                    entry.setRealName(user.getRealName());
                    entry.setRole(user.getRole());
                }
            }
            entry.setAction(action);
            entry.setTargetType(targetType);
            entry.setTargetId(targetId);
            entry.setDetail(detail != null && detail.length() > MAX_DETAIL_LENGTH
                    ? detail.substring(0, MAX_DETAIL_LENGTH) : detail);
            entry.setIp(clientIp());
            save(entry);
        } catch (Exception e) {
            // 日志失败不影响主流程
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
    }

    /** 登录场景无登录态，显式传入账号信息；失败时 user 可能为 null */
    public void recordLogin(SysUser user, String username, boolean success, String detail) {
        try {
            OperationLog entry = new OperationLog();
            if (user != null) {
                entry.setUserId(user.getId());
                entry.setUsername(user.getUsername());
                entry.setRealName(user.getRealName());
                entry.setRole(user.getRole());
            } else {
                entry.setUsername(username);
            }
            entry.setAction(success ? "login_success" : "login_fail");
            entry.setTargetType("sys_user");
            entry.setTargetId(user == null ? null : user.getId());
            entry.setDetail(detail);
            entry.setIp(clientIp());
            save(entry);
        } catch (Exception e) {
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
    }

    public IPage<OperationLog> page(long pageNum, long pageSize, String action,
                                    String username, LocalDate start, LocalDate end) {
        return page(new Page<>(pageNum, pageSize), Wrappers.<OperationLog>lambdaQuery()
                .eq(action != null && !action.isBlank(), OperationLog::getAction, action)
                .like(username != null && !username.isBlank(), OperationLog::getUsername, username)
                .ge(start != null, OperationLog::getCreateTime, start == null ? null : start.atStartOfDay())
                .lt(end != null, OperationLog::getCreateTime, end == null ? null : end.plusDays(1).atStartOfDay())
                .orderByDesc(OperationLog::getCreateTime));
    }

    private String clientIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return null;
    }
}
