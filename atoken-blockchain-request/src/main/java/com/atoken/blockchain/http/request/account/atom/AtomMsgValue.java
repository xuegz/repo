package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomMsgValue
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomMsgValue {
    private List<AtomAmount> amount;

    private String toAddress;

    private String fromAddress;

    private String delegatorAddress;

    private String validatorAddress;


    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToAddress() {
        return this.toAddress;
    }

    public void setAmount(List<AtomAmount> amount) {
        this.amount = amount;
    }

    public List<AtomAmount> getAmount() {
        return this.amount;
    }

    public String getDelegatorAddress() {
        return delegatorAddress;
    }

    public void setDelegatorAddress(String delegatorAddress) {
        this.delegatorAddress = delegatorAddress;
        this.fromAddress = delegatorAddress;
    }

    public String getValidatorAddress() {
        return validatorAddress;
    }

    public void setValidatorAddress(String validatorAddress) {
        this.validatorAddress = validatorAddress;
        this.toAddress = validatorAddress;
    }
}
