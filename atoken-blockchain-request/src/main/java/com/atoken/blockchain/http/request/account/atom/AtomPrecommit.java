package com.atoken.blockchain.http.request.account.atom;


import com.atoken.blockchain.utils.DateUtil;

import java.util.Date;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomPrecommit
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomPrecommit {
    private String round;

    private String signature;

    private String validatorAddress;

    private String validatorIndex;

    private Integer type;

    private AtomBlockId blockId;

    private String height;

    private Date timestamp;


    public void setTimestamp(String timestamp) {
        Integer index = timestamp.indexOf('.');
        this.timestamp = DateUtil.strToDate(timestamp.substring(0, index == -1? timestamp.length() - 1: index) + "Z", DateUtil.FORMAT_ALL);
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight() {
        return this.height;
    }

    public void setBlockId(AtomBlockId blockId) {
        this.blockId = blockId;
    }

    public AtomBlockId getBlockId() {
        return this.blockId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    public void setValidatorIndex(String validatorIndex) {
        this.validatorIndex = validatorIndex;
    }

    public String getValidatorIndex() {
        return this.validatorIndex;
    }

    public void setValidatorAddress(String validatorAddress) {
        this.validatorAddress = validatorAddress;
    }

    public String getValidatorAddress() {
        return this.validatorAddress;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getRound() {
        return this.round;
    }

}
