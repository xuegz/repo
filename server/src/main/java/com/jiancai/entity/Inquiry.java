package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 询价线索（客户留资，商户报价跟进）
 */
@Data
@TableName("inquiry")
public class Inquiry {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long merchantId;
    private String quantity;
    private String contact;
    private String remark;
    /** 期望到货时间，客户自由填写 */
    private String expectDate;
    private String openid;
    /** 0=待报价 1=已报价 2=跟进中 */
    private Integer status;
    private BigDecimal quotePrice;
    private BigDecimal quoteTotal;
    private String quoteRemark;
    private LocalDate quoteValidUntil;
    private LocalDateTime quoteTime;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String productImage;

    @TableField(exist = false)
    private String merchantName;

    @TableField(exist = false)
    private String merchantPhone;
}