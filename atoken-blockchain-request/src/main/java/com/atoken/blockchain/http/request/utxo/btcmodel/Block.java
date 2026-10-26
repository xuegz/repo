package com.atoken.blockchain.http.request.utxo.btcmodel;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class Block<T> extends BlockHeader{
    protected Long strippedSize;
    protected Long size;
    protected Long weight;
    protected Long height;
    protected List<T> tx;

    public List<T> getTx() {
        return tx;
    }

    public void setTx(List<T> tx) {
        this.tx = tx;
    }

    public Long getStrippedSize() {
        return strippedSize;
    }

    @JSONField(name = "strippedsize")
    public void setStrippedSize(Long strippedSize) {
        this.strippedSize = strippedSize;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }
}
