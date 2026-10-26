package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String name;
    private String brand;
    private String origin;
    private String spec;
    private String typeName;
    private BigDecimal price;
    private String unit;
    /** 数据库中以逗号分隔存储 */
    private String images;
    private String description;
    /** 0=待审核 1=在售 2=拒绝 3=下架 */
    private Integer status;
    private String rejectReason;
    private Integer views;
    private Integer forwards;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 非数据库字段：图片数组（返回前端时展开） */
    @TableField(exist = false)
    private List<String> imageList;

    /** 非数据库字段：品类名称 */
    @TableField(exist = false)
    private String categoryName;

    /** 非数据库字段：商户名称 */
    @TableField(exist = false)
    private String merchantName;

    /** 非数据库字段：商户电话 */
    @TableField(exist = false)
    private String merchantPhone;
}
