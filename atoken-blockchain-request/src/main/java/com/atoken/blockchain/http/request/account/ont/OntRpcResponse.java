package com.atoken.blockchain.http.request.account.ont;

import java.math.BigDecimal;

public class OntRpcResponse<T> {
    private String desc;
    private Integer error;
    private String jsonrpc;
    private Integer id;
    private T result;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static class StringResponse extends OntRpcResponse<String> {}
    public static class BigDecimalResponse extends OntRpcResponse<BigDecimal> {}
}
