package com.atoken.update.entity.dot;

import java.math.BigDecimal;
import java.util.Date;

public class DotValidator {
    private Long id;

    private String addr;

    private Short status;

    private BigDecimal tokens;

    private Double commissionRate;

    private Double annualReturn;

    private Integer allDelegatorNum;

    private Integer orderNum;

    private String keybaseUrl;

    private Integer points;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public BigDecimal getTokens() {
        return tokens;
    }

    public void setTokens(BigDecimal tokens) {
        this.tokens = tokens;
    }

    public Double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Double getAnnualReturn() {
        return annualReturn;
    }

    public void setAnnualReturn(Double annualReturn) {
        this.annualReturn = annualReturn;
    }

    public Integer getAllDelegatorNum() {
        return allDelegatorNum;
    }

    public void setAllDelegatorNum(Integer allDelegatorNum) {
        this.allDelegatorNum = allDelegatorNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getKeybaseUrl() {
        return keybaseUrl;
    }

    public void setKeybaseUrl(String keybaseUrl) {
        this.keybaseUrl = keybaseUrl == null ? null : keybaseUrl.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}