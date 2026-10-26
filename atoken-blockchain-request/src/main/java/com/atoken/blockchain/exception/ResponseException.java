package com.atoken.blockchain.exception;

import org.apache.http.HttpResponse;

/**
 * Created by qingyun.yu on 2018/11/21.
 */
public class ResponseException extends HttpException {
    private HttpResponse response;
    public ResponseException(Integer statusCode, String message, HttpResponse response) {
        super(statusCode, message);
        this.response = response;
    }

    public HttpResponse response() {
        return this.response;
    }
}
