package com.atoken.blockchain.http.request.account.atom;

import java.math.BigDecimal;

public class AtomDelegation {
    private String delegator_address;

    private String validator_address;

    private BigDecimal shares;

    public String getDelegatorAddress() {
        return delegator_address;
    }

    public void setDelegatorAddress(String delegatorAddress) {
        this.delegator_address = delegatorAddress;
    }

    public String getValidatorAddress() {
        return validator_address;
    }

    public void setValidatorAddress(String validatorAddress) {
        this.validator_address = validatorAddress;
    }

    public BigDecimal getShares() {
        return shares;
    }

    public void setShares(BigDecimal shares) {
        this.shares = shares;
    }

    public String getDelegator_address() {
        return delegator_address;
    }

    public void setDelegator_address(String delegator_address) {
        this.delegator_address = delegator_address;
    }

    public String getValidator_address() {
        return validator_address;
    }

    public void setValidator_address(String validator_address) {
        this.validator_address = validator_address;
    }
}
