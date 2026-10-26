package com.atoken.update.entity.coin.account;

import com.atoken.update.enums.FeeType;

import java.math.BigDecimal;
import java.util.Date;

public class Tx {
    protected Long id;

    protected String hash;

    protected String fromAddr;

    protected String toAddr;

    protected BigDecimal fee;

    protected BigDecimal amount;

    protected String blockHash;

    protected Integer blockHeight;

    protected Short blockState;

    protected Long timestamp;

    protected Date createTime;

    protected Date updateTime;

    protected Integer state;

    protected String assetShortName;

    protected Integer status;

    protected Integer confirm;

    protected BigDecimal gasLimit;

    protected BigDecimal usedGasLimit;

    protected BigDecimal gasPrice;

    protected String txId;

    protected String type;

    protected Integer platType;

    protected Integer txNum;

    protected String comment;

    protected FeeType feeType;

    protected Integer nonce;

    protected String mainCoin;

    protected boolean saveTx = true;

    protected boolean sendMessage = true;

    protected BigDecimal tip;

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr == null ? null : fromAddr.trim();
    }

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr == null ? null : toAddr.trim();
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash == null ? null : blockHash.trim();
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Short getBlockState() {
        return blockState;
    }

    public void setBlockState(Short blockState) {
        this.blockState = blockState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAssetShortName() {
        return assetShortName;
    }

    public void setAssetShortName(String assetShortName) {
        this.assetShortName = assetShortName == null ? null : assetShortName.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getConfirm() {
        return confirm;
    }

    public void setConfirm(Integer confirm) {
        this.confirm = confirm;
    }

    public BigDecimal getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigDecimal gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigDecimal getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigDecimal gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId == null ? null : txId.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getPlatType() {
        return platType;
    }

    public void setPlatType(Integer platType) {
        this.platType = platType;
    }

    public Integer getTxNum() {
        return txNum;
    }

    public void setTxNum(Integer txNum) {
        this.txNum = txNum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public String getMainCoin() {
        return mainCoin;
    }

    public void setMainCoin(String mainCoin) {
        this.mainCoin = mainCoin;
    }

    public boolean isSaveTx() {
        return saveTx;
    }

    public void setSaveTx(boolean saveTx) {
        this.saveTx = saveTx;
    }

    public BigDecimal getUsedGasLimit() {
        return usedGasLimit;
    }

    public void setUsedGasLimit(BigDecimal usedGasLimit) {
        this.usedGasLimit = usedGasLimit;
    }

    public boolean isSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
    }

    public BigDecimal getTip() {
        return tip;
    }

    public void setTip(BigDecimal tip) {
        this.tip = tip;
    }
}