package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.MpUser;
import com.jiancai.service.MpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序用户资料（昵称、头像）
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpUserController {

    private final MpUserService mpUserService;

    /** 查询资料；未设置过时返回空对象 */
    @GetMapping("/profile")
    public R<Map<String, Object>> profile(@RequestParam String openid) {
        String id = requireOpenid(openid);
        MpUser user = mpUserService.findByOpenid(id);
        Map<String, Object> data = new HashMap<>(4);
        if (user != null) {
            data.put("nickname", user.getNickname());
            data.put("avatar", user.getAvatar());
        }
        return R.ok(data);
    }

    /** 保存资料：nickname / avatar 可单独更新 */
    @PostMapping("/profile")
    public R<Map<String, Object>> save(@RequestBody Map<String, Object> body) {
        String openid = requireOpenid(text(body.get("openid")));
        String nickname = body.containsKey("nickname") ? text(body.get("nickname")).trim() : null;
        String avatar = body.containsKey("avatar") ? text(body.get("avatar")).trim() : null;

        if (nickname != null && nickname.codePointCount(0, nickname.length()) > 20) {
            throw new BusinessException("昵称最多 20 个字");
        }
        if (avatar != null && !avatar.isEmpty()
                && !avatar.startsWith("http://") && !avatar.startsWith("https://")
                && !avatar.startsWith("/upload/")) {
            throw new BusinessException("头像地址不合法");
        }
        if (avatar != null && avatar.length() > 500) {
            throw new BusinessException("头像地址过长");
        }

        MpUser user = mpUserService.saveProfile(openid, nickname, avatar);

        Map<String, Object> data = new HashMap<>(4);
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        return R.ok(data);
    }

    private static String requireOpenid(String openid) {
        String value = text(openid).trim();
        if (value.isEmpty()) {
            throw new BusinessException("缺少用户标识");
        }
        if (value.length() > 64) {
            throw new BusinessException("用户标识不合法");
        }
        return value;
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
