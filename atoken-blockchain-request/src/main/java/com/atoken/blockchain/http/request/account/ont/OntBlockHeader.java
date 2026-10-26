package com.atoken.blockchain.http.request.account.ont;

import java.math.BigInteger;
import java.util.List;

public class OntBlockHeader {
    private String transactionsRoot;
    private String consensusPayload;
    private Integer version;
    private List<String> sigData;
    private String prevBlockHash;
    private String nextBookkeeper;
    private List<String> bookkeepers;
    private Integer height;
    private BigInteger consensusData;
    private String hash;
    private Long timestamp;
    private String blockRoot;

    public String getTransactionsRoot() {
        return transactionsRoot;
    }

    public void setTransactionsRoot(String transactionsRoot) {
        this.transactionsRoot = transactionsRoot;
    }

    public String getConsensusPayload() {
        return consensusPayload;
    }

    public void setConsensusPayload(String consensusPayload) {
        this.consensusPayload = consensusPayload;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<String> getSigData() {
        return sigData;
    }

    public void setSigData(List<String> sigData) {
        this.sigData = sigData;
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public void setPrevBlockHash(String prevBlockHash) {
        this.prevBlockHash = prevBlockHash;
    }

    public String getNextBookkeeper() {
        return nextBookkeeper;
    }

    public void setNextBookkeeper(String nextBookkeeper) {
        this.nextBookkeeper = nextBookkeeper;
    }

    public List<String> getBookkeepers() {
        return bookkeepers;
    }

    public void setBookkeepers(List<String> bookkeepers) {
        this.bookkeepers = bookkeepers;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public BigInteger getConsensusData() {
        return consensusData;
    }

    public void setConsensusData(BigInteger consensusData) {
        this.consensusData = consensusData;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBlockRoot() {
        return blockRoot;
    }

    public void setBlockRoot(String blockRoot) {
        this.blockRoot = blockRoot;
    }
}
