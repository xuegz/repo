package com.atoken.blockchain.http.request.account.ont;

import com.github.ontio.common.Helper;

public class OntPayLoad {
    private String code;
    private OntTransfer transfer;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
//        transfer = OntTransfer.parseTransfer(this.toBytes());
    }

    public OntTransfer getTransfer() {
        return transfer;
    }

    public byte[] toBytes() {
        return Helper.hexToBytes(this.code);
    }
}
