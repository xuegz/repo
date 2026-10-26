package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户 / 门店
 */
@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String contactName;
    private String phone;
    /** 所属建材市场，如 大明宫建材城 */
    private String market;
    private String province;
    private String city;
    private String address;
    private String logo;
    private String intro;
    private Long categoryId;
    /** 0=待审核 1=已通过 2=已拒绝 */
    private Integer status;
    private Integer views;
    private Integer forwards;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 非数据库字段：主营品类名称 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String categoryName;

    /** 非数据库字段：产品数量 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer productCount;
}
