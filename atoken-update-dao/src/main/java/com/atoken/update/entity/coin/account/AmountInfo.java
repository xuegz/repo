package com.atoken.update.entity.coin.account;

import java.math.BigDecimal;

public class AmountInfo {
    private BigDecimal delegationAmount;
    private BigDecimal unBondingAmount;
    private BigDecimal rewardsAmount;

    public BigDecimal getDelegationAmount() {
        return delegationAmount;
    }

    public void setDelegationAmount(BigDecimal delegationAmount) {
        this.delegationAmount = delegationAmount;
    }

    public BigDecimal getUnBondingAmount() {
        return unBondingAmount;
    }

    public void setUnBondingAmount(BigDecimal unBondingAmount) {
        this.unBondingAmount = unBondingAmount;
    }

    public BigDecimal getRewardsAmount() {
        return rewardsAmount;
    }

    public void setRewardsAmount(BigDecimal rewardsAmount) {
        this.rewardsAmount = rewardsAmount;
    }
}
