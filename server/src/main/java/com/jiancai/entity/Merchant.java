package com.jiancai.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    /** 所属建材市场名称（market_id 的冗余字段，便于展示与旧数据兼容） */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String market;
    /** 所属建材市场 ID */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long marketId;
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
    private Integer calls;
    /** 门店坐标（用于小程序导航，可空） */
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private LocalDateTime expireTime;
    /** 专属邀请码（首次进入邀请页时生成）；仅在邀请接口单独返回，不随商户公开信息下发 */
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String inviteCode;
    /** 邀请人商户 ID（入驻时填写邀请码写入） */
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private Long inviterId;
    private LocalDateTime inviteTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 非数据库字段：主营品类名称 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String categoryName;

    /** 非数据库字段：产品数量 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer productCount;

    /** 非数据库字段：与当前定位的距离（公里） */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Double distance;

    /** 非数据库字段：门店资料保存时清除坐标 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Boolean clearLocation;

    /** 非数据库字段：当前访问者是否会员（非会员时联系方式与坐标已脱敏） */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Boolean memberActive;

    /** 非数据库字段：邀请人商户名称 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String inviterName;

    /** 非数据库字段：累计邀请入驻商户数 */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer inviteCount;
}
