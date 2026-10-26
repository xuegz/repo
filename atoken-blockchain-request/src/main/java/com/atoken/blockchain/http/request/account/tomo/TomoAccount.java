package com.atoken.blockchain.http.request.account.tomo;

import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoAccount
 * @Package com.ninedish.domain.vo
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoAccount {
    private String code;

    private String accountName;

    private Integer inTxCount;

    private Boolean isToken;

    private Integer logCount;

    private String createdAt;

    private BigDecimal balanceNumber;

    private String balance;

    private Integer totalTxCount;

    private Integer transactionCount;

    private String updatedAt;

    private Boolean hasTrc20;

    private Integer outTxCount;

    private Integer rewardCount;

    private Integer tokenTxCount;

    private Boolean hasTrc21;

    private Integer internalTxCount;

    private String contract;

    private String token;

    private Boolean isContract;

    private String fromTxn;

    private Integer minedBlock;

    private String id;

    private Boolean hasTrc721;

    private String hash;

    private Boolean status;


    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHasTrc721(Boolean hasTrc721) {
        this.hasTrc721 = hasTrc721;
    }

    public Boolean getHasTrc721() {
        return this.hasTrc721;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setMinedBlock(Integer minedBlock) {
        this.minedBlock = minedBlock;
    }

    public Integer getMinedBlock() {
        return this.minedBlock;
    }

    public void setFromTxn(String fromTxn) {
        this.fromTxn = fromTxn;
    }

    public String getFromTxn() {
        return this.fromTxn;
    }

    public void setIsContract(Boolean isContract) {
        this.isContract = isContract;
    }

    public Boolean getIsContract() {
        return this.isContract;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getContract() {
        return this.contract;
    }

    public void setInternalTxCount(Integer internalTxCount) {
        this.internalTxCount = internalTxCount;
    }

    public Integer getInternalTxCount() {
        return this.internalTxCount;
    }

    public void setHasTrc21(Boolean hasTrc21) {
        this.hasTrc21 = hasTrc21;
    }

    public Boolean getHasTrc21() {
        return this.hasTrc21;
    }

    public void setTokenTxCount(Integer tokenTxCount) {
        this.tokenTxCount = tokenTxCount;
    }

    public Integer getTokenTxCount() {
        return this.tokenTxCount;
    }

    public void setRewardCount(Integer rewardCount) {
        this.rewardCount = rewardCount;
    }

    public Integer getRewardCount() {
        return this.rewardCount;
    }

    public void setOutTxCount(Integer outTxCount) {
        this.outTxCount = outTxCount;
    }

    public Integer getOutTxCount() {
        return this.outTxCount;
    }

    public void setHasTrc20(Boolean hasTrc20) {
        this.hasTrc20 = hasTrc20;
    }

    public Boolean getHasTrc20() {
        return this.hasTrc20;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public Integer getTransactionCount() {
        return this.transactionCount;
    }

    public void setTotalTxCount(Integer totalTxCount) {
        this.totalTxCount = totalTxCount;
    }

    public Integer getTotalTxCount() {
        return this.totalTxCount;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalanceNumber(BigDecimal balanceNumber) {
        this.balanceNumber = balanceNumber;
    }

    public BigDecimal getBalanceNumber() {
        return this.balanceNumber;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setLogCount(Integer logCount) {
        this.logCount = logCount;
    }

    public Integer getLogCount() {
        return this.logCount;
    }

    public void setIsToken(Boolean isToken) {
        this.isToken = isToken;
    }

    public Boolean getIsToken() {
        return this.isToken;
    }

    public void setInTxCount(Integer inTxCount) {
        this.inTxCount = inTxCount;
    }

    public Integer getInTxCount() {
        return this.inTxCount;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
