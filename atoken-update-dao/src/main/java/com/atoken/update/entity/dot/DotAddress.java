package com.atoken.update.entity.dot;

import com.atoken.update.entity.Address;

import java.math.BigDecimal;

public class DotAddress extends Address {
    private BigDecimal delegateAmount;

    private BigDecimal unbondingAmount;

    private BigDecimal rewardsAmount;

    private String accountNum;

    private String type;

    private String stakeStatus;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStakeStatus() {
        return stakeStatus;
    }

    public void setStakeStatus(String stakeStatus) {
        this.stakeStatus = stakeStatus;
    }

    public BigDecimal getDelegateAmount() {
        return delegateAmount;
    }

    public void setDelegateAmount(BigDecimal delegateAmount) {
        this.delegateAmount = delegateAmount;
    }

    public BigDecimal getUnbondingAmount() {
        return unbondingAmount;
    }

    public void setUnbondingAmount(BigDecimal unbondingAmount) {
        this.unbondingAmount = unbondingAmount;
    }

    public BigDecimal getRewardsAmount() {
        return rewardsAmount;
    }

    public void setRewardsAmount(BigDecimal rewardsAmount) {
        this.rewardsAmount = rewardsAmount;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }
}
