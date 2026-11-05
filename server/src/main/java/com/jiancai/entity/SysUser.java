package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账号（平台管理员 / 商户）
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;

    /** 密码不对外输出 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String realName;
    private String phone;
    /** 1=平台管理员 2=商户 */
    private Integer role;
    private Long merchantId;
    /** 1=启用 0=禁用 */
    private Integer status;
    /** 1=首次登录需修改密码 */
    private Integer mustChangePassword;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
