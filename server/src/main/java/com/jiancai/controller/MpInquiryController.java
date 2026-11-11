package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.entity.Inquiry;
import com.jiancai.service.MpInquiryService;
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
 * 小程序询价接口：提交线索（会员专享）与「我的询价」（无需登录，按 openid 查询）
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpInquiryController {

    private final MpInquiryService mpInquiryService;

    /** 提交询价线索：客户留资，商户端跟进（会员专享） */
    @PostMapping("/inquiry")
    public R<Void> inquiry(@RequestBody(required = false) Map<String, Object> body) {
        mpInquiryService.submit(text(body, "openid"),
                number(body, "productId"), number(body, "merchantId"),
                text(body, "quantity"), text(body, "contact"),
                text(body, "remark"), text(body, "expectDate"));
        return R.ok();
    }

    /** 我的询价（含商户报价） */
    @GetMapping("/inquiries")
    public R<List<Inquiry>> myInquiries(@RequestParam String openid,
                                        @RequestParam(required = false) Integer status) {
        return R.ok(mpInquiryService.myInquiries(openid, status));
    }

    private static String text(Map<String, Object> body, String key) {
        Object value = body == null ? null : body.get(key);
        if (value == null) {
            return null;
        }
        String s = value.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private static Long number(Map<String, Object> body, String key) {
        String s = text(body, key);
        if (s == null) {
            return null;
        }
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
