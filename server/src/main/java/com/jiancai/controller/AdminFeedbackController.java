package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Feedback;
import com.jiancai.service.ChatService;
import com.jiancai.service.FeedbackService;
import com.jiancai.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 平台端 - 意见反馈与会话举报处理
 */
@RestController
@RequestMapping("/api/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final FeedbackService feedbackService;
    private final ChatService chatService;
    private final OperationLogService operationLogService;

    @GetMapping("/page")
    public R<IPage<Feedback>> page(@RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "10") long pageSize,
                                   @RequestParam(required = false) Integer source,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String keyword) {
        return R.ok(feedbackService.adminPage(pageNum, pageSize, source, status, keyword));
    }

    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        return R.ok(feedbackService.stats());
    }

    @PostMapping("/handle")
    public R<Void> handle(@RequestBody Map<String, Object> body) {
        Long id = toLong(body.get("id"));
        Integer status = toInt(body.get("status"));
        feedbackService.handle(id, status, text(body.get("reply")));
        operationLogService.record("feedback_handle", "feedback", id,
                status != null && status == 1 ? "标记反馈为已处理" : "重新打开反馈");
        return R.ok();
    }

    /** 举报关联会话内容，供平台判责 */
    @GetMapping("/chat/{conversationId}")
    public R<List<Map<String, Object>>> chat(@PathVariable Long conversationId) {
        return R.ok(chatService.adminMessages(conversationId));
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private static Long toLong(Object value) {
        if (value == null || value.toString().isBlank()) {
            throw new BusinessException("参数格式不正确");
        }
        try {
            return Long.valueOf(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("参数格式不正确");
        }
    }

    private static Integer toInt(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("参数格式不正确");
        }
    }
}
