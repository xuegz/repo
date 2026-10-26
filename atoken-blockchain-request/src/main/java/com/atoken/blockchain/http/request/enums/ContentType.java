package com.atoken.blockchain.http.request.enums;

/**
 * Created by Administrator on 2018/11/21.
 */
public enum ContentType {
    JSON("application/json"),
    JSON_UTF8("application/json;charset=utf-8"),
    WWW_FORM_URLENCODED("application/x-www-form-urlencoded");
    private String contentType;
    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public String contentType() {
        return this.contentType;
    }
}
