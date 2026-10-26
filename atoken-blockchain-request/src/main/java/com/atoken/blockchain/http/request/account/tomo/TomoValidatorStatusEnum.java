package com.atoken.blockchain.http.request.account.tomo;

public enum TomoValidatorStatusEnum {
    BOUNDED(2, "绑定"), BOUNDING(2, "绑定中"), UNBOUND(0, "未绑定");
    private short status;
    private String description;

    TomoValidatorStatusEnum(int status, String description) {
        this.status = (short) status;
        this.description = description;
    }


    public Short status() {
        return status;
    }
}
