package com.atoken.blockchain.http.request.utxo.btcmodel;

import com.alibaba.fastjson.annotation.JSONField;

public class BlockHeader {
    protected String hash;
    protected Integer confirmations;
    protected Long version;
    protected String versionHex;
    protected String merkleRoot;
    protected Long time;
    protected Long medianTime;
    protected String nonce;
    protected String bits;
    protected Double difficulty;
    protected String chainWork;
    protected String previousBlockHash;
    protected String nextBlockHash;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getVersionHex() {
        return versionHex;
    }

    public void setVersionHex(String versionHex) {
        this.versionHex = versionHex;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    @JSONField(name = "merkleroot")
    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getMedianTime() {
        return medianTime;
    }

    @JSONField(name = "mediantime")
    public void setMedianTime(Long medianTime) {
        this.medianTime = medianTime;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBits() {
        return bits;
    }

    public void setBits(String bits) {
        this.bits = bits;
    }

    public Double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }

    public String getChainWork() {
        return chainWork;
    }

    @JSONField(name = "chainwork")
    public void setChainWork(String chainWork) {
        this.chainWork = chainWork;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    @JSONField(name = "previousblockhash")
    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getNextBlockHash() {
        return nextBlockHash;
    }

    public void setNextBlockHash(String nextBlockHash) {
        this.nextBlockHash = nextBlockHash;
    }
}
