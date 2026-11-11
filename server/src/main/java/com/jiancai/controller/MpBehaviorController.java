package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Product;
import com.jiancai.service.MpHistoryService;
import com.jiancai.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 小程序行为上报与浏览记录：浏览 / 转发 / 电话联系 / 历史（无需登录）
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpBehaviorController {

    private final StatService statService;
    private final MpHistoryService mpHistoryService;

    /** 上报浏览：targetType 1=产品 2=商户 */
    @PostMapping("/view")
    public R<Void> view(@RequestBody(required = false) Map<String, Object> body) {
        statService.recordView(requiredTargetType(body), requiredTargetId(body), optionalOpenid(body));
        return R.ok();
    }

    /** 上报转发 */
    @PostMapping("/forward")
    public R<Void> forward(@RequestBody(required = false) Map<String, Object> body) {
        statService.recordForward(requiredTargetType(body), requiredTargetId(body), optionalOpenid(body));
        return R.ok();
    }

    /** 上报电话联系：targetType 1=产品 2=商户，merchantId 可不传由后端推导 */
    @PostMapping("/call")
    public R<Void> call(@RequestBody(required = false) Map<String, Object> body) {
        statService.recordCall(requiredTargetType(body), requiredTargetId(body),
                optionalLong(body, "merchantId"), optionalOpenid(body));
        return R.ok();
    }

    /** 我的浏览记录（按最近的在前） */
    @GetMapping("/history")
    public R<List<Product>> history(@RequestParam String openid) {
        return R.ok(mpHistoryService.list(openid));
    }

    /** 清除我的浏览记录（不影响收藏与询价） */
    @PostMapping("/history/clear")
    public R<Void> clearHistory(@RequestBody Map<String, Object> body) {
        mpHistoryService.clear(optionalOpenid(body));
        return R.ok();
    }

    private Integer requiredTargetType(Map<String, Object> body) {
        Long value = optionalLong(body, "targetType");
        if (value == null) {
            throw new BusinessException(400, "缺少目标类型");
        }
        if (value != 1 && value != 2) {
            throw new BusinessException(400, "目标类型不合法");
        }
        return value.intValue();
    }

    private Long requiredTargetId(Map<String, Object> body) {
        Long id = optionalLong(body, "targetId");
        if (id == null || id <= 0) {
            throw new BusinessException(400, "缺少目标编号");
        }
        return id;
    }

    private Long optionalLong(Map<String, Object> body, String key) {
        Object value = body == null ? null : body.get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException e) {
            throw new BusinessException(400, "参数格式不正确");
        }
    }

    private String optionalOpenid(Map<String, Object> body) {
        Object value = body == null ? null : body.get("openid");
        if (value == null) {
            return null;
        }
        String openid = value.toString().trim();
        if (openid.isEmpty()) {
            return null;
        }
        if (openid.length() > 64) {
            throw new BusinessException(400, "用户标识不合法");
        }
        return openid;
    }
}
