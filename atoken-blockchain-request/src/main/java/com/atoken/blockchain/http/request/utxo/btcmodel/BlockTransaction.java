package com.atoken.blockchain.http.request.utxo.btcmodel;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class BlockTransaction<VIN, VOUT> {
    protected String txId;
    protected String hash;
    protected Integer version;
    protected Integer size;
    protected Integer vSize;
    protected Long lockTime;
    protected List<VIN> vIns;
    protected List<VOUT> vOuts;
    protected String hex;
    protected String blockHash;
    protected Integer confirmations;
    protected Long time;
    protected Long blockTime;
    protected Long height;

    public String getTxId() {
        return txId;
    }

    @JSONField(name = "txid")
    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getvSize() {
        return vSize;
    }

    @JSONField(name = "vsize")
    public void setvSize(Integer vSize) {
        this.vSize = vSize;
    }

    public Long getLockTime() {
        return lockTime;
    }

    @JSONField(name = "locktime")
    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public List<VIN> getvIns() {
        return vIns;
    }

    @JSONField(name = "vin")
    public void setvIns(List<VIN> vIns) {
        this.vIns = vIns;
    }

    public List<VOUT> getvOuts() {
        return vOuts;
    }

    @JSONField(name = "vout")
    public void setvOuts(List<VOUT> vOuts) {
        this.vOuts = vOuts;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getBlockHash() {
        return blockHash;
    }

    @JSONField(name = "blockhash")
    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getBlockTime() {
        return blockTime;
    }

    @JSONField(name = "blocktime")
    public void setBlockTime(Long blockTime) {
        this.blockTime = blockTime;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }
}
