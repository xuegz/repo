package com.atoken.update.entity.ont;

import com.atoken.update.enums.TransferType;

import java.math.BigDecimal;

public class TransferMessage {
    private String walletId; // 钱包id
    private String coinType; // 币种
    private String parentCoinType; // 主链币种
    private String txHash; // 交易hash
    private BigDecimal amount; // 交易金额，有正负
    private Integer timestamp; // 交易时间
    private Boolean read; // 是否已读，true：已读 false：未读
    private Integer blockHeight; // 区块高度
    private Boolean token; // 是否是token交易， true： 是 false：不是
    private String contractAddr; // 合约地址
    private Integer success; // 交易最终结果， 1：成功 0：失败
    private Integer txState; // 交易状态 1：确认 0：未确认 -1：超时
    private Integer refId; // 业务表id
    private BigDecimal fee; // 手续费，若为其他币种填0
    private String fromAddr; // 转账地址
    private Integer blockState; // 区块状态
    private String blockHash; // 区块hash
    private Integer transferType; // 1:入账；2:出帐
    private String toAddr; //入账地址



    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getParentCoinType() {
        return parentCoinType;
    }

    public void setParentCoinType(String parentCoinType) {
        this.parentCoinType = parentCoinType;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Boolean getToken() {
        return token;
    }

    public void setToken(Boolean token) {
        this.token = token;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getTxState() {
        return txState;
    }

    public void setTxState(Integer txState) {
        this.txState = txState;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    public Integer getBlockState() {
        return blockState;
    }

    public void setBlockState(Integer blockState) {
        this.blockState = blockState;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }

    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

    public static TransferMessage toTransferMessage(Tx tx, Address address, boolean isFrom, Token token) {
        TransferMessage transferMessage = new TransferMessage();
        transferMessage.setWalletId(address.getWalletId());
        transferMessage.setAmount(isFrom ? tx.getAmount().negate() : tx.getAmount());
        transferMessage.setParentCoinType(token.getName().toUpperCase());
        transferMessage.setCoinType(tx.getAssetShortName().toUpperCase());
        transferMessage.setTxHash(tx.getHash());
        transferMessage.setTimestamp(tx.getTimestamp().intValue());
        transferMessage.setBlockHeight(tx.getBlockHeight());
        transferMessage.setBlockHash(tx.getBlockHash());
        transferMessage.setBlockState(tx.getBlockState().intValue());
        transferMessage.setToken(token.getName().equals(tx.getAssetShortName()) ? false: true);
        transferMessage.setContractAddr(tx.getContractAddr());
        transferMessage.setRefId(tx.getId().intValue());
        transferMessage.setTxState(tx.getStatus());
        transferMessage.setSuccess(tx.getState());
        transferMessage.setFee(tx.getFee().multiply(tx.getFeeType().rate()));
        transferMessage.setTransferType(isFrom ? TransferType.PAY.type() : TransferType.INCOME.type());
        transferMessage.setFromAddr(tx.getFromAddr());
        transferMessage.setToAddr(tx.getToAddr());
        return transferMessage;
    }
}
