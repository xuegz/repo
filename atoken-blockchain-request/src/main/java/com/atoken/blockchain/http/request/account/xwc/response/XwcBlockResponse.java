package com.atoken.blockchain.http.request.account.xwc.response;

import com.atoken.blockchain.http.request.RpcResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/8 下午5:40
 */
public class XwcBlockResponse {
    private String previous;
    private Date timestamp;
    private BigDecimal trxfee;
    private String miner;
    @JsonProperty("transaction_merkle_root")
    private String transactionMerkleRoot;
    private List<String> extensions;

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getTrxfee() {
        return trxfee;
    }

    public void setTrxfee(BigDecimal trxfee) {
        this.trxfee = trxfee;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public String getTransactionMerkleRoot() {
        return transactionMerkleRoot;
    }

    public void setTransactionMerkleRoot(String transactionMerkleRoot) {
        this.transactionMerkleRoot = transactionMerkleRoot;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public String getNextSecretHash() {
        return nextSecretHash;
    }

    public void setNextSecretHash(String nextSecretHash) {
        this.nextSecretHash = nextSecretHash;
    }

    public String getPreviousSecret() {
        return previousSecret;
    }

    public void setPreviousSecret(String previousSecret) {
        this.previousSecret = previousSecret;
    }

    public String getMinerSignature() {
        return minerSignature;
    }

    public void setMinerSignature(String minerSignature) {
        this.minerSignature = minerSignature;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<String> transactions) {
        this.transactions = transactions;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public List<String> getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(List<String> transactionIds) {
        this.transactionIds = transactionIds;
    }

    @JsonProperty("next_secret_hash")
    private String nextSecretHash;
    @JsonProperty("previous_secret")
    private String previousSecret;
    @JsonProperty("miner_signature")
    private String minerSignature;
    private List<String> transactions;
    private Integer number;
    @JsonProperty("block_id")
    private String blockId;
    @JsonProperty("signing_key")
    private String signingKey;
    private String reward;
    @JsonProperty("transaction_ids")
    private List<String> transactionIds;

    public static class XwcBlockRpcResponse extends RpcResponse<XwcBlockResponse> {
    }
}
