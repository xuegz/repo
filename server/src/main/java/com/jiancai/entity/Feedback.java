package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户意见反馈与会话举报
 */
@Data
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 1=意见反馈 2=会话举报 */
    private Integer source;
    private String openid;
    private Long merchantId;
    private String merchantName;
    private Long conversationId;
    /** 功能建议 / 问题反馈 / 举报投诉 */
    private String category;
    private String content;
    private String contact;
    /** 0=待处理 1=已处理 */
    private Integer status;
    private String reply;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;

    /** 后台列表展示的微信昵称，不落库 */
    @TableField(exist = false)
    private String userName;
}
