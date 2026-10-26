package com.atoken.blockchain.http.request.account.atom;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomEstimateInfo
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomEstimateInfo<A> {
    @JSONField(name = "delegator_address")
    private String delegatorAddress;

    private A amount;

    @JSONField(name = "validator_address")
    private String validatorAddress;

    @JSONField(name = "base_req")
    private AtomBaseReq baseReq;


    public void setBaseReq(AtomBaseReq baseReq) {
        this.baseReq = baseReq;
    }

    public AtomBaseReq getBaseReq() {
        return this.baseReq;
    }

    public void setValidatorAddress(String validatorAddress) {
        this.validatorAddress = validatorAddress;
    }

    public String getValidatorAddress() {
        return this.validatorAddress;
    }

    public void setAmount(A amount) {
        this.amount = amount;
    }

    public A getAmount() {
        return this.amount;
    }

    public void setDelegatorAddress(String delegatorAddress) {
        this.delegatorAddress = delegatorAddress;
    }

    public String getDelegatorAddress() {
        return this.delegatorAddress;
    }

}
