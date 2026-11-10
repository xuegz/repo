package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.SysUser;
import com.jiancai.security.UserContext;
import com.jiancai.service.OperationLogService;
import com.jiancai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台端 - 子账号管理（运营/审核等共用平台后台的账号）
 */
@RestController
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public R<List<SysUser>> list(@RequestParam(required = false) String keyword) {
        return R.ok(userService.lambdaQuery()
                .eq(SysUser::getRole, 1)
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(SysUser::getUsername, keyword.trim())
                        .or().like(SysUser::getRealName, keyword.trim())
                        .or().like(SysUser::getPhone, keyword.trim()))
                .orderByAsc(SysUser::getId)
                .list());
    }

    /** 新增子账号，返回一次性临时密码（首次登录强制修改） */
    @PostMapping("/create")
    public R<Map<String, Object>> create(@RequestBody Map<String, String> body) {
        String username = trim(body.get("username"));
        String realName = trim(body.get("realName"));
        String phone = trim(body.get("phone"));
        if (username.isEmpty()) {
            throw new BusinessException("请填写登录账号");
        }
        if (realName.isEmpty()) {
            throw new BusinessException("请填写姓名");
        }
        if (userService.lambdaQuery().eq(SysUser::getUsername, username).count() > 0) {
            throw new BusinessException("账号 " + username + " 已存在");
        }
        String tempPassword = userService.generateTempPassword();
        Long id = userService.createUser(username, tempPassword, realName,
                phone.isEmpty() ? null : phone, 1, null, true);
        operationLogService.record("account_create", "sys_user", id,
                "新增平台子账号「" + username + "／" + realName + "」");
        Map<String, Object> result = new HashMap<>(4);
        result.put("id", id);
        result.put("username", username);
        result.put("password", tempPassword);
        return R.ok(result);
    }

    /** 重置子账号密码，返回一次性临时密码 */
    @PostMapping("/reset-password")
    public R<Map<String, Object>> resetPassword(@RequestBody Map<String, Object> body) {
        Long id = parseId(body.get("id"));
        SysUser user = requireAdmin(id);
        String tempPassword = userService.generateTempPassword();
        userService.resetPassword(id, tempPassword);
        operationLogService.record("account_reset_password", "sys_user", id,
                "重置平台子账号「" + user.getUsername() + "」密码");
        Map<String, Object> result = new HashMap<>(4);
        result.put("username", user.getUsername());
        result.put("password", tempPassword);
        return R.ok(result);
    }

    /** 启用/禁用子账号 */
    @PostMapping("/status")
    public R<Void> status(@RequestBody Map<String, Object> body) {
        Long id = parseId(body.get("id"));
        Integer status = Integer.valueOf(String.valueOf(body.get("status")));
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值不合法");
        }
        if (status == 0 && id.equals(UserContext.getUserId())) {
            throw new BusinessException("不能禁用当前登录账号");
        }
        SysUser user = requireAdmin(id);
        SysUser update = new SysUser();
        update.setId(id);
        update.setStatus(status);
        userService.updateById(update);
        operationLogService.record("account_status", "sys_user", id,
                (status == 1 ? "启用" : "禁用") + "平台子账号「" + user.getUsername() + "」");
        return R.ok();
    }

    private SysUser requireAdmin(Long id) {
        SysUser user = userService.getById(id);
        if (user == null || !Integer.valueOf(1).equals(user.getRole())) {
            throw new BusinessException("子账号不存在");
        }
        return user;
    }

    private Long parseId(Object value) {
        if (value == null) {
            throw new BusinessException("缺少账号 ID");
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            throw new BusinessException("账号 ID 格式不正确");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
