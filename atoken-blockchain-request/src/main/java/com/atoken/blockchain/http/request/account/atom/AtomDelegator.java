package com.atoken.blockchain.http.request.account.atom;

import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomDelegator
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomDelegator {
    private BigDecimal shares;

    private String delegatorAddress;

    private String validatorAddress;


    public void setValidatorAddress(String validatorAddress) {
        this.validatorAddress = validatorAddress;
    }

    public String getValidatorAddress() {
        return this.validatorAddress;
    }

    public void setDelegatorAddress(String delegatorAddress) {
        this.delegatorAddress = delegatorAddress;
    }

    public String getDelegatorAddress() {
        return this.delegatorAddress;
    }

    public void setShares(BigDecimal shares) {
        this.shares = shares;
    }

    public BigDecimal getShares() {
        return this.shares;
    }

}
