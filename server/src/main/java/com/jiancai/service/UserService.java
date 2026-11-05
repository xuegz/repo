package com.jiancai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.SysUser;
import com.jiancai.mapper.SysUserMapper;
import com.jiancai.security.JwtUtil;
import com.jiancai.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 账号业务
 */
@Service
public class UserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginAttemptService loginAttemptService;

    /** 登录：返回 token 与用户信息 */
    public Map<String, Object> login(String username, String password) {
        String account = username == null ? "" : username.trim();
        loginAttemptService.check(account);
        SysUser user = lambdaQuery().eq(SysUser::getUsername, account).one();
        if (user == null || !encoder.matches(password, user.getPassword())) {
            int remaining = loginAttemptService.recordFailure(account);
            if (remaining == 0) {
                throw new BusinessException("密码错误次数过多，请 10 分钟后再试");
            }
            throw new BusinessException("账号或密码错误，还可尝试 " + remaining + " 次");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系平台");
        }
        loginAttemptService.clear(account);
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getMerchantId());

        Map<String, Object> data = new HashMap<>(8);
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("role", user.getRole());
        data.put("realName", user.getRealName());
        data.put("merchantId", user.getMerchantId());
        data.put("mustChangePassword", Integer.valueOf(1).equals(user.getMustChangePassword()));
        return data;
    }

    /** 创建账号（商户入驻/审核通过时自动开通） */
    public Long createUser(String username, String rawPassword, String realName,
                          String phone, Integer role, Long merchantId) {
        return createUser(username, rawPassword, realName, phone, role, merchantId, false);
    }

    public Long createUser(String username, String rawPassword, String realName,
                          String phone, Integer role, Long merchantId, boolean mustChangePassword) {
        long exists = lambdaQuery().eq(SysUser::getUsername, username).count();
        if (exists > 0) {
            throw new BusinessException("账号 " + username + " 已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole(role);
        user.setMerchantId(merchantId);
        user.setStatus(1);
        user.setMustChangePassword(mustChangePassword ? 1 : 0);
        save(user);
        return user.getId();
    }

    /** 自助修改密码：校验原密码，修改后清除首次登录标记 */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new BusinessException("请输入原密码");
        }
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException("请输入新密码");
        }
        if (newPassword.length() < 6 || newPassword.length() > 32) {
            throw new BusinessException("新密码长度需为 6-32 位");
        }
        if (newPassword.equals(oldPassword)) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        user.setPassword(encoder.encode(newPassword));
        user.setMustChangePassword(0);
        updateById(user);
    }

    /** 平台重置密码：重置为临时密码，并强制下次登录修改 */
    public void resetPassword(Long userId, String rawPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(encoder.encode(rawPassword));
        user.setMustChangePassword(1);
        updateById(user);
    }
}
