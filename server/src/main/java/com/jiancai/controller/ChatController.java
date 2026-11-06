package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.service.ChatService;
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

    /** 打开与某商户的会话（不存在时创建） */
    @PostMapping("/open")
    public R<Map<String, Object>> open(@RequestBody Map<String, Object> body) {
        return R.ok(chatService.openConversation(text(body.get("openid")), toLong(body.get("merchantId"))));
    }

    @GetMapping("/list")
    public R<List<Map<String, Object>>> list(@RequestParam String openid) {
        return R.ok(chatService.listForUser(openid));
    }

    @GetMapping("/messages")
    public R<List<Map<String, Object>>> messages(@RequestParam String openid,
                                                 @RequestParam Long conversationId,
                                                 @RequestParam(required = false) Long afterId) {
        return R.ok(chatService.messages(conversationId, openid, null, afterId));
    }

    @PostMapping("/send")
    public R<Map<String, Object>> send(@RequestBody Map<String, Object> body) {
        return R.ok(chatService.sendAsUser(text(body.get("openid")), toLong(body.get("conversationId")),
                text(body.get("type")), text(body.get("content")), toLong(body.get("productId"))));
    }

    @PostMapping("/read")
    public R<Void> read(@RequestBody Map<String, Object> body) {
        chatService.markReadAsUser(text(body.get("openid")), toLong(body.get("conversationId")));
        return R.ok();
    }

    /** 举报会话：写入操作日志供平台核查 */
    @PostMapping("/report")
    public R<Void> report(@RequestBody Map<String, Object> body) {
        chatService.reportConversation(text(body.get("openid")), toLong(body.get("conversationId")),
                text(body.get("reason")));
        return R.ok();
    }

    /** 消息 tab 未读红点用的汇总数 */
    @GetMapping("/unread")
    public R<Integer> unread(@RequestParam String openid) {
        return R.ok(chatService.unreadForUser(openid));
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
