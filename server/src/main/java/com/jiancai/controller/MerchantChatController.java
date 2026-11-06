package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.security.UserContext;
import com.jiancai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商户端站内消息（小程序商家中心）
 */
@RestController
@RequestMapping("/api/merchant/chat")
@RequiredArgsConstructor
public class MerchantChatController {

    private final ChatService chatService;

    @GetMapping("/list")
    public R<List<Map<String, Object>>> list() {
        return R.ok(chatService.listForMerchant(UserContext.getMerchantId()));
    }

    @GetMapping("/messages")
    public R<List<Map<String, Object>>> messages(@RequestParam Long conversationId,
                                                 @RequestParam(required = false) Long afterId) {
        return R.ok(chatService.messages(conversationId, null, UserContext.getMerchantId(), afterId));
    }

    @PostMapping("/send")
    public R<Map<String, Object>> send(@RequestBody Map<String, Object> body) {
        return R.ok(chatService.sendAsMerchant(UserContext.getMerchantId(), toLong(body.get("conversationId")),
                text(body.get("type")), text(body.get("content")), toLong(body.get("productId"))));
    }

    @PostMapping("/read")
    public R<Void> read(@RequestBody Map<String, Object> body) {
        chatService.markReadAsMerchant(UserContext.getMerchantId(), toLong(body.get("conversationId")));
        return R.ok();
    }

    @GetMapping("/unread")
    public R<Integer> unread() {
        return R.ok(chatService.unreadForMerchant(UserContext.getMerchantId()));
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
