package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端操作日志
 */
@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String realName;
    /** 1=平台管理员 2=商户 */
    private Integer role;
    /** merchant_audit / merchant_reset_password / product_audit */
    private String action;
    /** merchant / product */
    private String targetType;
    private Long targetId;
    private String detail;
    private String ip;
    private LocalDateTime createTime;
}
