package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 电话联系记录（1=产品 2=商户）
 */
@Data
@TableName("call_record")
public class CallRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer targetType;
    private Long targetId;
    /** 被联系的商户，冗余字段便于商户端查询 */
    private Long merchantId;
    private String openid;
    private LocalDateTime createTime;
}
