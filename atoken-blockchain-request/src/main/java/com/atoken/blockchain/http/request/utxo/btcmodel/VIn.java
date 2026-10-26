package com.atoken.blockchain.http.request.utxo.btcmodel;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class VIn<S extends ScriptSig> {
    protected String txId;
    protected Integer vOut;
    protected S scriptSig;
    protected List<String> txInWitness;
    protected Long sequence;
    protected String coinBase;

    public String getTxId() {
        return txId;
    }

    @JSONField(name = "txid")
    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Integer getvOut() {
        return vOut;
    }

    @JSONField(name = "vout")
    public void setvOut(Integer vOut) {
        this.vOut = vOut;
    }

    public S getScriptSig() {
        return scriptSig;
    }

    @JSONField(name = "scriptSig")
    public void setScriptSig(S scriptSig) {
        this.scriptSig = scriptSig;
    }

    public List<String> getTxInWitness() {
        return txInWitness;
    }

    @JSONField(name = "txinwitness")
    public void setTxInWitness(List<String> txInWitness) {
        this.txInWitness = txInWitness;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getCoinBase() {
        return coinBase;
    }

    @JSONField(name = "coinbase")
    public void setCoinBase(String coinBase) {
        this.coinBase = coinBase;
    }
}
