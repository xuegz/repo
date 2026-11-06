package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiancai.common.R;
import com.jiancai.entity.Inquiry;
import com.jiancai.service.AdminInquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/**
 * 平台端 - 全平台询价线索
 */
@RestController
@RequestMapping("/api/admin/inquiry")
public class AdminInquiryController {

    @Autowired
    private AdminInquiryService adminInquiryService;

    @GetMapping("/page")
    public R<IPage<Inquiry>> page(@RequestParam(defaultValue = "1") long pageNum,
                                  @RequestParam(defaultValue = "10") long pageSize,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Long merchantId,
                                  @RequestParam(required = false) String merchantName,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return R.ok(adminInquiryService.page(pageNum, pageSize, keyword, status,
                merchantId, merchantName, startDate, endDate));
    }

    @GetMapping("/overview")
    public R<Map<String, Object>> overview() {
        return R.ok(adminInquiryService.overview());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) Long merchantId,
                                         @RequestParam(required = false) String merchantName,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] data = adminInquiryService.exportCsv(keyword, status, merchantId, merchantName,
                startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inquiries-" + LocalDate.now() + ".csv\"")
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .body(data);
    }
}
