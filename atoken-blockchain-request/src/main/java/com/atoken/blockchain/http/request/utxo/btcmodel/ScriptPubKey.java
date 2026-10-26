package com.atoken.blockchain.http.request.utxo.btcmodel;

import java.util.List;

public class ScriptPubKey {
    protected String asm;
    protected String hex;
    protected Integer reqSigs;
    protected String type;
    protected List<String> addresses;

    public String getAsm() {
        return asm;
    }

    public void setAsm(String asm) {
        this.asm = asm;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public Integer getReqSigs() {
        return reqSigs;
    }

    public void setReqSigs(Integer reqSigs) {
        this.reqSigs = reqSigs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
}
