package com.atoken.blockchain.http.request.enums;

/**
 * Created by Administrator on 2018/11/21.
 */
public enum ResponseStatus {
    OK(200, "OK (HTTP/1.0 - RFC 1945)");
    private Integer status;
    private String message;
    ResponseStatus(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer status() {
        return this.status;
    }

    public String message() {
        return this.message;
    }
}
