package com.atoken.blockchain.exception;

/**
 *
 * @author Administrator
 * @date 2018/11/21
 */
public class ParseException extends RuntimeException {
    private String data;
    public ParseException(String data, String errorMessage) {
        super(errorMessage);
        this.data = data;
    }

    public String data() {
        return this.data;
    }
}
