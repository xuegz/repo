package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序 openid 与商户账号的绑定关系
 */
@Data
@TableName("mp_merchant_bind")
public class MpMerchantBind {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private Long userId;
    private Long merchantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
