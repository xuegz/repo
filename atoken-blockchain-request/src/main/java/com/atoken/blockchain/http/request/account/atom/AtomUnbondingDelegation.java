package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomUnbondingDelegation
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomUnbondingDelegation {
    private String delegatorAddress;

    private List<AtomEntries> entries;

    private String validatorAddress;


    public void setValidatorAddress(String validatorAddress) {
        this.validatorAddress = validatorAddress;
    }

    public String getValidatorAddress() {
        return this.validatorAddress;
    }

    public void setEntries(List<AtomEntries> entries) {
        this.entries = entries;
    }

    public List<AtomEntries> getEntries() {
        return this.entries;
    }

    public void setDelegatorAddress(String delegatorAddress) {
        this.delegatorAddress = delegatorAddress;
    }

    public String getDelegatorAddress() {
        return this.delegatorAddress;
    }

}
