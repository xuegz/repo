package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页轮播 / 头图
 */
@Data
@TableName("banner")
public class Banner {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String image;
    private String link;
    private Integer sort;
    /** 1=启用 0=停用 */
    private Integer status;
    private LocalDateTime createTime;
}
