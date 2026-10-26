package com.atoken.blockchain.http.request;

import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.account.ont.OntRpcResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/11 下午2:03
 */
public class XwcRpcResponse<T> {
    private String desc;

    public JSONObject getError() {
        return error;
    }

    public void setError(JSONObject error) {
        this.error = error;
    }

    private JSONObject error;
    private String jsonrpc;
    private Integer id;
    private T result;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
    public static class ListStringResponse extends RpcResponse<List<String>> {}
    public static class BigDecimalResponse extends OntRpcResponse<BigDecimal> {}
}
