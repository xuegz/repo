package com.atoken.update.entity.dot;

import java.math.BigDecimal;
import java.util.Date;

public class DotDelegateDetail {
    private Long id;

    private String walletId;

    private String addr;

    private String validatorAddr;

    private BigDecimal delegateAmount;

    private BigDecimal rewardsAmount;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId == null ? null : walletId.trim();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public String getValidatorAddr() {
        return validatorAddr;
    }

    public void setValidatorAddr(String validatorAddr) {
        this.validatorAddr = validatorAddr == null ? null : validatorAddr.trim();
    }

    public BigDecimal getDelegateAmount() {
        return delegateAmount;
    }

    public void setDelegateAmount(BigDecimal delegateAmount) {
        this.delegateAmount = delegateAmount;
    }

    public BigDecimal getRewardsAmount() {
        return rewardsAmount;
    }

    public void setRewardsAmount(BigDecimal rewardsAmount) {
        this.rewardsAmount = rewardsAmount;
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