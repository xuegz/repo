package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpMerchantBind;
import com.jiancai.entity.SysUser;
import com.jiancai.mapper.MpMerchantBindMapper;
import com.jiancai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序商户身份：账号密码绑定、会话续期、解绑
 */
@Service
@RequiredArgsConstructor
public class MpMerchantBindService {

    private static final int ROLE_MERCHANT = 2;

    private final MpMerchantBindMapper bindMapper;
    private final UserService userService;
    private final MerchantService merchantService;
    private final JwtUtil jwtUtil;

    public MpMerchantBind findByOpenid(String openid) {
        return bindMapper.selectOne(Wrappers.<MpMerchantBind>lambdaQuery()
                .eq(MpMerchantBind::getOpenid, requireOpenid(openid)));
    }

    /** 用商户账号密码完成绑定，返回登录 token 与店铺信息 */
    public Map<String, Object> bind(String openid, String username, String password) {
        String id = requireOpenid(openid);
        if (username == null || username.isBlank()) {
            throw new BusinessException("请输入商户账号");
        }
        if (password == null || password.isBlank()) {
            throw new BusinessException("请输入密码");
        }

        // 复用平台登录：密码校验、失败锁定、账号禁用均由既有逻辑处理
        Map<String, Object> session = userService.login(username.trim(), password);
        Long userId = toLong(session.get("userId"));
        Integer role = session.get("role") == null ? null : Integer.valueOf(session.get("role").toString());
        Long merchantId = toLong(session.get("merchantId"));
        if (!Integer.valueOf(ROLE_MERCHANT).equals(role) || merchantId == null) {
            throw new BusinessException(403, "该账号不是商户账号");
        }
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null || !Integer.valueOf(1).equals(merchant.getStatus())) {
            throw new BusinessException("商户未上线，请联系平台");
        }

        MpMerchantBind bind = findByOpenid(id);
        LocalDateTime now = LocalDateTime.now();
        if (bind == null) {
            bind = new MpMerchantBind();
            bind.setOpenid(id);
            bind.setUserId(userId);
            bind.setMerchantId(merchantId);
            bind.setCreateTime(now);
            bind.setUpdateTime(now);
            bindMapper.insert(bind);
        } else {
            bind.setUserId(userId);
            bind.setMerchantId(merchantId);
            bind.setUpdateTime(now);
            bindMapper.updateById(bind);
        }

        Map<String, Object> data = new HashMap<>(8);
        data.put("bound", true);
        data.put("token", session.get("token"));
        data.put("merchantId", merchantId);
        data.put("shopName", merchant.getName());
        data.put("shopLogo", merchant.getLogo());
        data.put("realName", session.get("realName"));
        data.put("role", role);
        data.put("mustChangePassword", Boolean.TRUE.equals(session.get("mustChangePassword")));
        return data;
    }

    /** 查询绑定状态并签发新 token；账号或商户失效时返回 bound=false */
    public Map<String, Object> session(String openid) {
        String id = requireOpenid(openid);
        Map<String, Object> data = new HashMap<>(8);
        MpMerchantBind bind = findByOpenid(id);
        if (bind == null) {
            data.put("bound", false);
            return data;
        }
        SysUser user = userService.getById(bind.getUserId());
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())
                || !Integer.valueOf(ROLE_MERCHANT).equals(user.getRole())) {
            data.put("bound", false);
            data.put("reason", "账号已停用，请联系平台");
            return data;
        }
        Merchant merchant = merchantService.getById(bind.getMerchantId());
        if (merchant == null || !Integer.valueOf(1).equals(merchant.getStatus())) {
            data.put("bound", false);
            data.put("reason", "商户未上线，请联系平台");
            return data;
        }

        data.put("bound", true);
        data.put("token", jwtUtil.generateToken(user.getId(), user.getRole(), user.getMerchantId()));
        data.put("merchantId", merchant.getId());
        data.put("shopName", merchant.getName());
        data.put("shopLogo", merchant.getLogo());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("mustChangePassword", Integer.valueOf(1).equals(user.getMustChangePassword()));
        return data;
    }

    /** 解绑当前微信身份，仅允许解绑自己的绑定 */
    public void unbind(Long userId, String openid) {
        MpMerchantBind bind = findByOpenid(openid);
        if (bind == null) {
            return;
        }
        if (userId == null || !userId.equals(bind.getUserId())) {
            throw new BusinessException(403, "无权解绑该商户身份");
        }
        bindMapper.deleteById(bind.getId());
    }

    private static String requireOpenid(String openid) {
        String value = openid == null ? "" : openid.trim();
        if (value.isEmpty()) {
            throw new BusinessException("缺少用户标识");
        }
        if (value.length() > 64) {
            throw new BusinessException("用户标识不合法");
        }
        return value;
    }

    private static Long toLong(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }
}
