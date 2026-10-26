package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.utils.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomValidator
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomValidator {
    private Boolean jailed;

    private String consensusPubkey;

    private String operatorAddress;

    private AtomDescription description;

    private BigDecimal tokens;

    private AtomCommission commission;

    private String unbondingHeight;

    private BigDecimal delegatorShares;

    private Date unbondingTime;

    private String minSelfDelegation;

    private Integer status;


    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setMinSelfDelegation(String minSelfDelegation) {
        this.minSelfDelegation = minSelfDelegation;
    }

    public String getMinSelfDelegation() {
        return this.minSelfDelegation;
    }

    public void setUnbondingTime(String unbondingTime) {
        Integer index = unbondingTime.indexOf('.');
        this.unbondingTime = DateUtil.strToDate(unbondingTime.substring(0,  index == -1? unbondingTime.length() - 1: index) + "Z", DateUtil.FORMAT_ALL);
    }

    public Date getUnbondingTime() {
        return this.unbondingTime;
    }

    public void setDelegatorShares(BigDecimal delegatorShares) {
        this.delegatorShares = delegatorShares;
    }

    public BigDecimal getDelegatorShares() {
        return this.delegatorShares;
    }

    public void setUnbondingHeight(String unbondingHeight) {
        this.unbondingHeight = unbondingHeight;
    }

    public String getUnbondingHeight() {
        return this.unbondingHeight;
    }

    public void setCommission(AtomCommission commission) {
        this.commission = commission;
    }

    public AtomCommission getCommission() {
        return this.commission;
    }

    public void setTokens(BigDecimal tokens) {
        this.tokens = tokens;
    }

    public BigDecimal getTokens() {
        return this.tokens;
    }

    public void setDescription(AtomDescription description) {
        this.description = description;
    }

    public AtomDescription getDescription() {
        return this.description;
    }

    public void setOperatorAddress(String operatorAddress) {
        this.operatorAddress = operatorAddress;
    }

    public String getOperatorAddress() {
        return this.operatorAddress;
    }

    public void setConsensusPubkey(String consensusPubkey) {
        this.consensusPubkey = consensusPubkey;
    }

    public String getConsensusPubkey() {
        return this.consensusPubkey;
    }

    public void setJailed(Boolean jailed) {
        this.jailed = jailed;
    }

    public Boolean getJailed() {
        return this.jailed;
    }

}
