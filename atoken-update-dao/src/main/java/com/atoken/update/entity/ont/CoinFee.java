package com.atoken.update.entity.ont;

import java.math.BigDecimal;
import java.util.Date;

public class CoinFee {
    private Integer id;

    private String symbol;

    private BigDecimal rate;

    private Integer blockTxNum;

    private Integer blockSize;

    private Integer mempoolTxNum;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getBlockTxNum() {
        return blockTxNum;
    }

    public void setBlockTxNum(Integer blockTxNum) {
        this.blockTxNum = blockTxNum;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getMempoolTxNum() {
        return mempoolTxNum;
    }

    public void setMempoolTxNum(Integer mempoolTxNum) {
        this.mempoolTxNum = mempoolTxNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}