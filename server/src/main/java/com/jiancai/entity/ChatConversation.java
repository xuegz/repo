package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户与商户的站内会话
 */
@Data
@TableName("chat_conversation")
public class ChatConversation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private Long merchantId;
    /** 用户未读条数 */
    private Integer userUnread;
    /** 商户未读条数 */
    private Integer merchantUnread;
    private String lastContent;
    private String lastContentType;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
