package com.jiancai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Feedback;
import com.jiancai.entity.MpUser;
import com.jiancai.mapper.FeedbackMapper;
import com.jiancai.mapper.MpUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 意见反馈与会话举报
 */
@Service
public class FeedbackService extends ServiceImpl<FeedbackMapper, Feedback> {

    @Autowired
    private MpUserMapper mpUserMapper;

    public void submit(String openid, String category, String content, String contact) {
        String text = content == null ? "" : content.trim();
        if (text.length() < 5) {
            throw new BusinessException("请至少填写 5 个字的反馈内容");
        }
        if (text.length() > 1000) {
            throw new BusinessException("反馈内容最多 1000 字");
        }
        String contactText = contact == null ? null : contact.trim();
        if (contactText != null && contactText.length() > 64) {
            throw new BusinessException("联系方式最多 64 字");
        }
        Feedback feedback = new Feedback();
        feedback.setSource(1);
        feedback.setOpenid(openid);
        feedback.setCategory(normalizeCategory(category));
        feedback.setContent(text);
        feedback.setContact(contactText == null || contactText.isEmpty() ? null : contactText);
        feedback.setStatus(0);
        save(feedback);
    }

    /** 会话举报：让平台有一份可处理、可回复的记录，同时保留操作日志 */
    public void report(String openid, Long conversationId, Long merchantId, String merchantName, String reason) {
        String detail = reason == null || reason.isBlank() ? "用户举报会话" : reason.trim();
        if (detail.length() > 200) {
            detail = detail.substring(0, 200);
        }
        Feedback feedback = new Feedback();
        feedback.setSource(2);
        feedback.setOpenid(openid);
        feedback.setMerchantId(merchantId);
        feedback.setMerchantName(merchantName);
        feedback.setConversationId(conversationId);
        feedback.setCategory("举报投诉");
        feedback.setContent(detail);
        feedback.setStatus(0);
        save(feedback);
    }

    /** 用户端「我的反馈」：含举报记录与平台回复 */
    public List<Feedback> listForUser(String openid) {
        return list(Wrappers.<Feedback>lambdaQuery()
                .eq(Feedback::getOpenid, openid)
                .orderByDesc(Feedback::getCreateTime)
                .last("LIMIT 20"));
    }

    public IPage<Feedback> adminPage(long pageNum, long pageSize, Integer source, Integer status, String keyword) {
        String kw = keyword == null ? null : keyword.trim();
        IPage<Feedback> page = page(new Page<>(pageNum, pageSize), Wrappers.<Feedback>lambdaQuery()
                .eq(source != null, Feedback::getSource, source)
                .eq(status != null, Feedback::getStatus, status)
                .and(kw != null && !kw.isEmpty(), w -> w.like(Feedback::getContent, kw)
                        .or().like(Feedback::getOpenid, kw)
                        .or().like(Feedback::getContact, kw)
                        .or().like(Feedback::getMerchantName, kw))
                .orderByDesc(Feedback::getCreateTime));
        fillUserNames(page.getRecords());
        return page;
    }

    public Map<String, Object> stats() {
        Map<String, Object> data = new HashMap<>(4);
        data.put("pending", count(Wrappers.<Feedback>lambdaQuery().eq(Feedback::getStatus, 0)));
        data.put("today", count(Wrappers.<Feedback>lambdaQuery()
                .ge(Feedback::getCreateTime, LocalDate.now().atStartOfDay())));
        data.put("total", count());
        return data;
    }

    public void handle(Long id, Integer status, String reply) {
        Feedback feedback = getById(id);
        if (feedback == null) {
            throw new BusinessException("反馈记录不存在");
        }
        int target = status != null && status == 1 ? 1 : 0;
        String replyText = reply == null ? "" : reply.trim();
        if (replyText.length() > 500) {
            throw new BusinessException("回复内容最多 500 字");
        }
        feedback.setStatus(target);
        feedback.setReply(replyText.isEmpty() ? null : replyText);
        feedback.setHandleTime(target == 1 ? LocalDateTime.now() : null);
        updateById(feedback);
    }

    private void fillUserNames(List<Feedback> rows) {
        List<String> openids = rows.stream()
                .map(Feedback::getOpenid)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .collect(Collectors.toList());
        if (openids.isEmpty()) {
            return;
        }
        Map<String, String> names = mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery()
                        .in(MpUser::getOpenid, openids)).stream()
                .filter(user -> user.getNickname() != null && !user.getNickname().isBlank())
                .collect(Collectors.toMap(MpUser::getOpenid, MpUser::getNickname, (a, b) -> a));
        for (Feedback row : rows) {
            row.setUserName(names.get(row.getOpenid()));
        }
    }

    private String normalizeCategory(String category) {
        String value = category == null ? "" : category.trim();
        if (value.isEmpty()) {
            return "功能建议";
        }
        if (value.length() > 32) {
            throw new BusinessException("反馈类型不合法");
        }
        return value;
    }
}
