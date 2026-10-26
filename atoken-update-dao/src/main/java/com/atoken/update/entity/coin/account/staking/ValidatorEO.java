package com.atoken.update.entity.coin.account.staking;

import java.math.BigDecimal;
import java.util.Date;

public class ValidatorEO {
    private Long id;

    private String addr;

    private String consensusPubkey;

    private Short jailed;

    private Short status;

    private BigDecimal tokens;

    private BigDecimal delegatorShares;

    private String moniker;

    private String identity;

    private String website;

    private String details;

    private Integer unbondingHeight;

    private Long unbondingTimestamp;

    private Double rate;

    private Double maxRate;

    private Double maxChangeRate;

    private Long rateUpdateTimestamp;

    private Double yearYield;

    private Integer allDelegatorNum;

    private Integer orderNum;

    private String keybaseUrl;

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

    public String getConsensusPubkey() {
        return consensusPubkey;
    }

    public void setConsensusPubkey(String consensusPubkey) {
        this.consensusPubkey = consensusPubkey == null ? null : consensusPubkey.trim();
    }

    public Short getJailed() {
        return jailed;
    }

    public void setJailed(Short jailed) {
        this.jailed = jailed;
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

    public BigDecimal getDelegatorShares() {
        return delegatorShares;
    }

    public void setDelegatorShares(BigDecimal delegatorShares) {
        this.delegatorShares = delegatorShares;
    }

    public String getMoniker() {
        return moniker;
    }

    public void setMoniker(String moniker) {
        this.moniker = moniker == null ? null : moniker.trim();
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity == null ? null : identity.trim();
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website == null ? null : website.trim();
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details == null ? null : details.trim();
    }

    public Integer getUnbondingHeight() {
        return unbondingHeight;
    }

    public void setUnbondingHeight(Integer unbondingHeight) {
        this.unbondingHeight = unbondingHeight;
    }

    public Long getUnbondingTimestamp() {
        return unbondingTimestamp;
    }

    public void setUnbondingTimestamp(Long unbondingTimestamp) {
        this.unbondingTimestamp = unbondingTimestamp;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(Double maxRate) {
        this.maxRate = maxRate;
    }

    public Double getMaxChangeRate() {
        return maxChangeRate;
    }

    public void setMaxChangeRate(Double maxChangeRate) {
        this.maxChangeRate = maxChangeRate;
    }

    public Long getRateUpdateTimestamp() {
        return rateUpdateTimestamp;
    }

    public void setRateUpdateTimestamp(Long rateUpdateTimestamp) {
        this.rateUpdateTimestamp = rateUpdateTimestamp;
    }

    public Double getYearYield() {
        return yearYield;
    }

    public void setYearYield(Double yearYield) {
        this.yearYield = yearYield;
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
}