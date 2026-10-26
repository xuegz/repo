package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.utils.DateUtil;

import java.util.Date;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomHeader
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomHeader {

    private String validatorsHash;

    private String chainId;

    private String consensusHash;

    private Integer totalTxs;

    private String proposerAddress;

    private String nextValidatorsHash;

    private AtomVersion version;

    private String dataHash;

    private String lastResultsHash;

    private AtomBlockId lastBlockId;

    private String evidenceHash;

    private Integer numTxs;

    private String appHash;

    private Date time;

    private Integer height;

    private String lastCommitHash;


    public void setLastCommitHash(String lastCommitHash) {
        this.lastCommitHash = lastCommitHash;
    }

    public String getLastCommitHash() {
        return this.lastCommitHash;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setTime(String time) {
        Integer index = time.indexOf('.');
        this.time = DateUtil.strToDate(time.substring(0,  index == -1? time.length() - 1: index) + "Z", DateUtil.FORMAT_ALL);
    }

    public Date getTime() {
        return this.time;
    }

    public void setAppHash(String appHash) {
        this.appHash = appHash;
    }

    public String getAppHash() {
        return this.appHash;
    }

    public void setNumTxs(Integer numTxs) {
        this.numTxs = numTxs;
    }

    public Integer getNumTxs() {
        return this.numTxs;
    }

    public void setEvidenceHash(String evidenceHash) {
        this.evidenceHash = evidenceHash;
    }

    public String getEvidenceHash() {
        return this.evidenceHash;
    }

    public void setLastBlockId(AtomBlockId lastBlockId) {
        this.lastBlockId = lastBlockId;
    }

    public AtomBlockId getLastBlockId() {
        return this.lastBlockId;
    }

    public void setLastResultsHash(String lastResultsHash) {
        this.lastResultsHash = lastResultsHash;
    }

    public String getLastResultsHash() {
        return this.lastResultsHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash;
    }

    public String getDataHash() {
        return this.dataHash;
    }

    public void setVersion(AtomVersion version) {
        this.version = version;
    }

    public AtomVersion getVersion() {
        return this.version;
    }

    public void setNextValidatorsHash(String nextValidatorsHash) {
        this.nextValidatorsHash = nextValidatorsHash;
    }

    public String getNextValidatorsHash() {
        return this.nextValidatorsHash;
    }

    public void setProposerAddress(String proposerAddress) {
        this.proposerAddress = proposerAddress;
    }

    public String getProposerAddress() {
        return this.proposerAddress;
    }

    public void setTotalTxs(Integer totalTxs) {
        this.totalTxs = totalTxs;
    }

    public Integer getTotalTxs() {
        return this.totalTxs;
    }

    public void setConsensusHash(String consensusHash) {
        this.consensusHash = consensusHash;
    }

    public String getConsensusHash() {
        return this.consensusHash;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getChainId() {
        return this.chainId;
    }

    public void setValidatorsHash(String validatorsHash) {
        this.validatorsHash = validatorsHash;
    }

    public String getValidatorsHash() {
        return this.validatorsHash;
    }

}
