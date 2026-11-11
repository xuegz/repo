package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.security.JwtUtil;
import com.jiancai.security.MpAuth;
import com.jiancai.service.ChatService;
import com.jiancai.service.MemberAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 小程序用户端站内消息
 */
@RestController
@RequestMapping("/api/mp/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final MemberAccessService memberAccessService;

    /** 打开与某商户的会话（不存在时创建） */
    @PostMapping("/open")
    public R<Map<String, Object>> open(@RequestHeader(value = "Authorization", required = false) String auth,
                                       @RequestBody Map<String, Object> body) {
        return R.ok(chatService.openConversation(authMemberOpenid(auth, text(body.get("openid"))),
                toLong(body.get("merchantId"))));
    }

    @GetMapping("/list")
    public R<List<Map<String, Object>>> list(@RequestHeader(value = "Authorization", required = false) String auth,
                                             @RequestParam(required = false) String openid) {
        return R.ok(chatService.listForUser(authMemberOpenid(auth, openid)));
    }

    @GetMapping("/messages")
    public R<List<Map<String, Object>>> messages(@RequestHeader(value = "Authorization", required = false) String auth,
                                                 @RequestParam(required = false) String openid,
                                                 @RequestParam Long conversationId,
                                                 @RequestParam(required = false) Long afterId,
                                                 @RequestParam(required = false) Long beforeId) {
        return R.ok(chatService.messages(conversationId, authMemberOpenid(auth, openid), null, afterId, beforeId));
    }

    @PostMapping("/send")
    public R<Map<String, Object>> send(@RequestHeader(value = "Authorization", required = false) String auth,
                                       @RequestBody Map<String, Object> body) {
        return R.ok(chatService.sendAsUser(authMemberOpenid(auth, text(body.get("openid"))),
                toLong(body.get("conversationId")), text(body.get("type")), text(body.get("content")),
                toLong(body.get("productId"))));
    }

    @PostMapping("/read")
    public R<Void> read(@RequestHeader(value = "Authorization", required = false) String auth,
                        @RequestBody Map<String, Object> body) {
        chatService.markReadAsUser(authMemberOpenid(auth, text(body.get("openid"))),
                toLong(body.get("conversationId")));
        return R.ok();
    }

    /** 举报会话：写入操作日志供平台核查 */
    @PostMapping("/report")
    public R<Void> report(@RequestHeader(value = "Authorization", required = false) String auth,
                          @RequestBody Map<String, Object> body) {
        chatService.reportConversation(authMemberOpenid(auth, text(body.get("openid"))),
                toLong(body.get("conversationId")), text(body.get("reason")));
        return R.ok();
    }

    /** 消息 tab 未读红点用的汇总数 */
    @GetMapping("/unread")
    public R<Integer> unread(@RequestHeader(value = "Authorization", required = false) String auth,
                             @RequestParam(required = false) String openid) {
        return R.ok(chatService.unreadForUser(authMemberOpenid(auth, openid)));
    }

    /** 校验用户 token（openid 仅用于前端兼容，必须与 token 一致），并在站内咨询为会员专享 */
    private String authMemberOpenid(String authorization, String claimed) {
        String openid = MpAuth.requireOpenid(jwtUtil, authorization, claimed);
        memberAccessService.requireMember(openid, "在线咨询");
        return openid;
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private static Long toLong(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("参数格式不正确");
        }
    }
}
