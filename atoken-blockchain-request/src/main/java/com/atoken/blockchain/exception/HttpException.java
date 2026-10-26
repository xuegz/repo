package com.atoken.blockchain.exception;

/**
 *
 * @author Administrator
 * @date 2018/11/18
 */
public class HttpException extends RuntimeException {
    private Integer statusCode;
    public HttpException(Integer statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer statusCode() {
        return this.statusCode;
    }
}
