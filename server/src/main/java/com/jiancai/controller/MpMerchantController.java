package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.security.UserContext;
import com.jiancai.service.MemberAccessService;
import com.jiancai.service.MpMerchantBindService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 小程序端商户身份：绑定、会话、解绑
 */
@RestController
@RequestMapping("/api/mp/merchant")
@RequiredArgsConstructor
public class MpMerchantController {

    private final MpMerchantBindService bindService;
    private final MemberAccessService memberAccessService;

    /** 商户账号密码绑定当前微信 */
    @PostMapping("/bind")
    public R<Map<String, Object>> bind(@RequestBody Map<String, Object> body) {
        return R.ok(bindService.bind(text(body.get("openid")), text(body.get("username")),
                text(body.get("password"))));
    }

    /** 查询绑定状态并续期 token */
    @GetMapping("/session")
    public R<Map<String, Object>> session(@RequestParam String openid) {
        return R.ok(bindService.session(openid));
    }

    /** 会员状态：小程序据此展示锁定态与开通/续费引导 */
    @GetMapping("/access")
    public R<Map<String, Object>> access(@RequestParam(required = false) String openid) {
        return R.ok(memberAccessService.status(openid));
    }

    /** 解绑当前微信与商户账号 */
    @PostMapping("/unbind")
    public R<Void> unbind(@RequestBody Map<String, Object> body) {
        bindService.unbind(UserContext.getUserId(), text(body.get("openid")));
        return R.ok();
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
