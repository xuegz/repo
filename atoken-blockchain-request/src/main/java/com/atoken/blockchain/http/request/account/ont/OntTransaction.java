package com.atoken.blockchain.http.request.account.ont;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class OntTransaction {
    private String nonce;
    private String payer;
    private Integer txType;
    private Integer version;
    private List<OntSig> Sigs;
    private List<String> attributes;
    private BigDecimal gasPrice;
    private OntPayLoad payload;
    private Integer height;
    private String hash;
    private BigDecimal gasLimit;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Integer getTxType() {
        return txType;
    }

    public void setTxType(Integer txType) {
        this.txType = txType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<OntSig> getSigs() {
        return Sigs;
    }

    public void setSigs(List<OntSig> sigs) {
        Sigs = sigs;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public BigDecimal getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigDecimal gasPrice) {
        this.gasPrice = gasPrice;
    }

    public OntPayLoad getPayload() {
        return payload;
    }

    public void setPayload(OntPayLoad payload) {
        this.payload = payload;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigDecimal getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigDecimal gasLimit) {
        this.gasLimit = gasLimit;
    }
}
