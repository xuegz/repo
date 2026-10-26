package com.atoken.blockchain.http.request.utxo.btcmodel;

public class BlockResponse<T> {
    private String id;
    private String error;
    private T result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
