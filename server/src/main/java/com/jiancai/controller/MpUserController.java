package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Favorite;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.MpUser;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.FavoriteMapper;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.ViewRecordMapper;
import com.jiancai.security.JwtUtil;
import com.jiancai.service.MemberAccessService;
import com.jiancai.service.MpUserService;
import com.jiancai.service.WxLoginService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序用户资料（昵称、头像）与「我的」页摘要
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpUserController {

    private final MpUserService mpUserService;
    private final ViewRecordMapper viewRecordMapper;
    private final FavoriteMapper favoriteMapper;
    private final InquiryMapper inquiryMapper;
    private final WxLoginService wxLoginService;
    private final JwtUtil jwtUtil;
    private final MemberAccessService memberAccessService;

    /** 微信登录：小程序 wx.login 的 code 换 openid，同时下发聊天 token */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        String code = body.get("code") == null ? "" : body.get("code").toString().trim();
        if (code.isEmpty()) {
            throw new BusinessException("缺少微信登录凭证");
        }
        if (code.length() > 128) {
            throw new BusinessException("微信登录凭证不合法");
        }
        Map<String, Object> session = wxLoginService.code2Session(code);
        Object openid = session == null ? null : session.get("openid");
        if (openid == null) {
            throw new BusinessException("微信登录失败，请重试");
        }
        String value = openid.toString();
        mpUserService.touchLogin(value);
        Map<String, Object> result = new HashMap<>(4);
        result.put("openid", value);
        result.put("token", jwtUtil.generateUserToken(value));
        Object unionid = session.get("unionid");
        if (unionid != null) {
            result.put("unionid", unionid);
        }
        return R.ok(result);
    }

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

    /** 「我的」页摘要：浏览/收藏/询价数量 + 注册序号 + 会员角色与到期时间 */
    @GetMapping("/mine/summary")
    public R<Map<String, Object>> summary(@RequestParam String openid) {
        String id = requireOpenid(openid);
        Map<String, Object> data = new HashMap<>(16);
        data.put("historyCount", viewRecordMapper.selectCount(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getOpenid, id)
                .eq(ViewRecord::getTargetType, 1)));
        data.put("favoriteCount", favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, id)));
        data.put("inquiryCount", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getOpenid, id)));

        MpUser user = mpUserService.findByOpenid(id);
        data.put("userNo", mpUserService.userNo(user));
        if (user != null) {
            data.put("registerTime", user.getCreateTime());
        }

        Map<String, Object> member = memberAccessService.status(id);
        data.put("bound", member.get("bound"));
        data.put("memberActive", member.get("memberActive"));
        if (member.get("shopName") != null) {
            data.put("shopName", member.get("shopName"));
        }
        if (member.get("expireTime") instanceof LocalDateTime expire) {
            data.put("expireTime", expire);
            // 按自然日计算剩余天数，避免“还剩 3 小时”显示成剩 0 天
            data.put("daysLeft", ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(),
                    expire.toLocalDate()));
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
