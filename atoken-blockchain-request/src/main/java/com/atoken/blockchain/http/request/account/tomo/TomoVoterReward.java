package com.atoken.blockchain.http.request.account.tomo;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoVoterReward
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoVoterReward {
    private BigDecimal reward;

    private String reason;

    private String address;

    private String validator;

    private Integer epoch;

    private Integer endBlock;

    private Date rewardTime;

    private Date createdAt;

    private Integer startBlock;

    private Integer signNumber;

    @JSONField(name = "__v")
    private Integer v;

    private String validatorName;

    private BigDecimal lockBalance;

    @JSONField(name = "_id")
    private String id;

    private Date updatedAt;


    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setLockBalance(BigDecimal lockBalance) {
        this.lockBalance = lockBalance;
    }

    public BigDecimal getLockBalance() {
        return this.lockBalance;
    }

    public void setValidatorName(String validatorName) {
        this.validatorName = validatorName;
    }

    public String getValidatorName() {
        return this.validatorName;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public Integer getV() {
        return this.v;
    }

    public void setSignNumber(Integer signNumber) {
        this.signNumber = signNumber;
    }

    public Integer getSignNumber() {
        return this.signNumber;
    }

    public void setStartBlock(Integer startBlock) {
        this.startBlock = startBlock;
    }

    public Integer getStartBlock() {
        return this.startBlock;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setRewardTime(Date rewardTime) {
        this.rewardTime = rewardTime;
    }

    public Date getRewardTime() {
        return this.rewardTime;
    }

    public void setEndBlock(Integer endBlock) {
        this.endBlock = endBlock;
    }

    public Integer getEndBlock() {
        return this.endBlock;
    }

    public void setEpoch(Integer epoch) {
        this.epoch = epoch;
    }

    public Integer getEpoch() {
        return this.epoch;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getValidator() {
        return this.validator;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReward(BigDecimal reward) {
        this.reward = reward;
    }

    public BigDecimal getReward() {
        return this.reward;
    }

}
