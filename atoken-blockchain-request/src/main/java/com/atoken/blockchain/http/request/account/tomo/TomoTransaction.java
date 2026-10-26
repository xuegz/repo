package com.atoken.blockchain.http.request.account.tomo;


/**
 * @author susky
 * @version V1.0
 * @Title: TomoTransaction
 * @Package com.ninedish.domain.vo
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoTransaction {
    private String blockHash;

    private String transactionIndex;

    private String nonce;

    private String input;

    private String r;

    private String s;

    private String v;

    private String blockNumber;

    private String gas;

    private String from;

    private String to;

    private String value;

    private String hash;

    private String gasPrice;


    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasPrice() {
        return this.gasPrice;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return this.to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGas() {
        return this.gas;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockNumber() {
        return this.blockNumber;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getV() {
        return this.v;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getS() {
        return this.s;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getR() {
        return this.r;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInput() {
        return this.input;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionIndex() {
        return this.transactionIndex;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockHash() {
        return this.blockHash;
    }

}
