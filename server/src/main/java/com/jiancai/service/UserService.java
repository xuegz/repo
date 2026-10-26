package com.jiancai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.SysUser;
import com.jiancai.mapper.SysUserMapper;
import com.jiancai.security.JwtUtil;
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

    /** 登录：返回 token 与用户信息 */
    public Map<String, Object> login(String username, String password) {
        SysUser user = lambdaQuery().eq(SysUser::getUsername, username).one();
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系平台");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getMerchantId());

        Map<String, Object> data = new HashMap<>(8);
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("role", user.getRole());
        data.put("realName", user.getRealName());
        data.put("merchantId", user.getMerchantId());
        return data;
    }

    /** 创建账号（商户入驻/审核通过时自动开通） */
    public Long createUser(String username, String rawPassword, String realName,
                          String phone, Integer role, Long merchantId) {
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
        save(user);
        return user.getId();
    }

    /** 重置密码 */
    public void resetPassword(Long userId, String rawPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(encoder.encode(rawPassword));
        updateById(user);
    }
}
