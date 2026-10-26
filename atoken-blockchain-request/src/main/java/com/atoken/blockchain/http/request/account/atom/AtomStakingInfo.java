package com.atoken.blockchain.http.request.account.atom;

public class AtomStakingInfo {
    private Long unbondingTime;
    private Integer maxValidators;
    private Integer maxEntries;
    private String bondDenom;

    public Long getUnbondingTime() {
        return unbondingTime;
    }

    public void setUnbondingTime(Long unbondingTime) {
        this.unbondingTime = unbondingTime;
    }

    public Integer getMaxValidators() {
        return maxValidators;
    }

    public void setMaxValidators(Integer maxValidators) {
        this.maxValidators = maxValidators;
    }

    public Integer getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(Integer maxEntries) {
        this.maxEntries = maxEntries;
    }

    public String getBondDenom() {
        return bondDenom;
    }

    public void setBondDenom(String bondDenom) {
        this.bondDenom = bondDenom;
    }
}
