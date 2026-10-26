package com.atoken.update.entity.ont;

import java.math.BigDecimal;

public class TxDetail {
    private Long id;

    private String txHash;

    private String walletId;

    private String addr;

    private BigDecimal amount;

    private Integer blockHeight;

    private String blockHash;

    private Short blockState;

    private Long timestamp;

    private Integer state;

    private String contractAddr;

    private BigDecimal fee;

    private Integer txNum;

    private Integer isEntry;

    private String txId;

    private Integer del;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId == null ? null : walletId.trim();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash == null ? null : blockHash.trim();
    }

    public Short getBlockState() {
        return blockState;
    }

    public void setBlockState(Short blockState) {
        this.blockState = blockState;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr == null ? null : contractAddr.trim();
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getTxNum() {
        return txNum;
    }

    public void setTxNum(Integer txNum) {
        this.txNum = txNum;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId == null ? null : txId.trim();
    }

    public Integer getIsEntry() {
        return isEntry;
    }

    public void setIsEntry(Integer isEntry) {
        this.isEntry = isEntry;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public static TxDetail txToTxDetail(Tx tx, boolean isFrom, Address address) {
        TxDetail txDetail = new TxDetail();
        txDetail.setTxHash(tx.getHash());
        txDetail.setBlockHash(tx.getBlockHash());
        txDetail.setContractAddr(tx.getContractAddr());
        txDetail.setTxId(tx.getTxId());
        txDetail.setFee(tx.getFee());
        txDetail.setTimestamp(tx.getTimestamp());
        txDetail.setState(tx.getState());
        txDetail.setBlockHeight(tx.getBlockHeight());
        txDetail.setWalletId(address.getWalletId());
        txDetail.setAddr(address.getAddr());
        txDetail.setIsEntry(!isFrom ? 0: 1);
        txDetail.setAmount(isFrom ? tx.getAmount().negate() : tx.getAmount());
        return txDetail;
    }
}