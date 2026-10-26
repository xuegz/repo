package com.atoken.update.entity.coin.account;

public class BlockchainConfig {
    protected Integer blockNumberLimit;
    protected Integer blockConfirm;
    protected Integer initBlockHeight;
    protected String mainAsset;
    protected Integer delayHeight;

    protected String blockchainAsset;
    protected boolean hasCoinFee;
    protected Integer maxRollbackCount;
    protected String tablePrefix;
    protected Integer threadPoolSize;
    protected Integer blockHistoryRetainTime;

    protected Integer handlerNum;
    protected Integer handlerTxRecordMaxThread;

    public Integer getHandlerNum() {
        return handlerNum;
    }

    public void setHandlerNum(Integer handlerNum) {
        this.handlerNum = handlerNum;
    }

    public Integer getHandlerTxRecordMaxThread() {
        return handlerTxRecordMaxThread;
    }

    public void setHandlerTxRecordMaxThread(Integer handlerTxRecordMaxThread) {
        this.handlerTxRecordMaxThread = handlerTxRecordMaxThread;
    }

    public Integer getBlockNumberLimit() {
        return blockNumberLimit;
    }

    public void setBlockNumberLimit(Integer blockNumberLimit) {
        this.blockNumberLimit = blockNumberLimit;
    }

    public Integer getBlockConfirm() {
        return blockConfirm;
    }

    public void setBlockConfirm(Integer blockConfirm) {
        this.blockConfirm = blockConfirm;
    }

    public Integer getInitBlockHeight() {
        return initBlockHeight;
    }

    public void setInitBlockHeight(Integer initBlockHeight) {
        this.initBlockHeight = initBlockHeight;
    }

    public String getMainAsset() {
        return mainAsset;
    }

    public void setMainAsset(String mainAsset) {
        this.mainAsset = mainAsset;
    }

    public String getBlockchainAsset() {
        return blockchainAsset;
    }

    public void setBlockchainAsset(String blockchainAsset) {
        this.blockchainAsset = blockchainAsset;
    }

    public boolean isHasCoinFee() {
        return hasCoinFee;
    }

    public void setHasCoinFee(boolean hasCoinFee) {
        this.hasCoinFee = hasCoinFee;
    }

    public Integer getMaxRollbackCount() {
        return maxRollbackCount;
    }

    public void setMaxRollbackCount(Integer maxRollbackCount) {
        this.maxRollbackCount = maxRollbackCount;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public Integer getDelayHeight() {
        return delayHeight;
    }

    public void setDelayHeight(Integer delayHeight) {
        this.delayHeight = delayHeight;
    }

    public Integer getBlockHistoryRetainTime() {
        return blockHistoryRetainTime;
    }

    public void setBlockHistoryRetainTime(Integer blockHistoryRetainTime) {
        this.blockHistoryRetainTime = blockHistoryRetainTime;
    }
}
