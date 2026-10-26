package com.atoken.blockchain.http.request.enums;

public enum AtomValidtorStatus {
    unbonded(0, "已解绑"), unbonding(1, "解绑中"), bonded(2, "已绑定");

    private Integer status;
    private String description;

    AtomValidtorStatus(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public String description() {
        return this.description;
    }

    public Integer status() {
        return this.status;
    }
}
