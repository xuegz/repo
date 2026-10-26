package com.jiancai.security;

import lombok.Data;

/**
 * 登录用户上下文信息
 */
@Data
public class LoginUser {

    /** 账号ID */
    private Long userId;

    /** 角色：1=平台管理员 2=商户 */
    private Integer role;

    /** 所属商户ID（管理员为空） */
    private Long merchantId;
}
