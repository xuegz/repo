package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 建材市场
 */
@Data
@TableName("market")
public class Market {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String province;
    private String city;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String logo;
    private String intro;
    private Integer sort;
    /** 0=停用 1=启用 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 非数据库字段：入驻商户数 */
    @TableField(exist = false)
    private Integer merchantCount;

    /** 非数据库字段：与当前定位的距离（公里） */
    @TableField(exist = false)
    private Double distance;
}
