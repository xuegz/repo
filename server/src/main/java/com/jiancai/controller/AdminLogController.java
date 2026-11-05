package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.OperationLog;
import com.jiancai.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 平台端 - 操作日志
 */
@RestController
@RequestMapping("/api/admin/log")
public class AdminLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/page")
    public R<IPage<OperationLog>> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String action,
                                       @RequestParam(required = false) String username,
                                       @RequestParam(required = false) String start,
                                       @RequestParam(required = false) String end) {
        return R.ok(operationLogService.page(pageNum, pageSize, action, username,
                parseDate(start), parseDate(end)));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException("日期格式应为 yyyy-MM-dd");
        }
    }
}
