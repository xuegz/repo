package com.atoken.blockchain.http.request.account.kar.subscan;

/**
 * @author DFG
 * @date 2021-07-28
 */
public class ExtrinsicResponse {

    Integer code;

    String message;

    Long ttl;

    ExtrinsicData data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public ExtrinsicData getData() {
        return data;
    }

    public void setData(ExtrinsicData data) {
        this.data = data;
    }
}
