package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.entity.Feedback;
import com.jiancai.security.JwtUtil;
import com.jiancai.security.MpAuth;
import com.jiancai.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 小程序用户端意见反馈
 */
@RestController
@RequestMapping("/api/mp/feedback")
@RequiredArgsConstructor
public class MpFeedbackController {

    private final FeedbackService feedbackService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public R<Void> submit(@RequestHeader(value = "Authorization", required = false) String auth,
                          @RequestBody Map<String, Object> body) {
        String openid = MpAuth.requireOpenid(jwtUtil, auth, text(body.get("openid")));
        feedbackService.submit(openid, text(body.get("category")), text(body.get("content")),
                text(body.get("contact")));
        return R.ok();
    }

    /** 我的反馈：包含平台回复与处理状态 */
    @GetMapping("/list")
    public R<List<Feedback>> list(@RequestHeader(value = "Authorization", required = false) String auth,
                                  @RequestParam(required = false) String openid) {
        return R.ok(feedbackService.listForUser(MpAuth.requireOpenid(jwtUtil, auth, openid)));
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
