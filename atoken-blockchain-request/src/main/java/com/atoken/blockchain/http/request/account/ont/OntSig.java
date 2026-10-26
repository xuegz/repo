package com.atoken.blockchain.http.request.account.ont;

import java.util.List;

public class OntSig {
    private List<String> pubKeys;
    private List<String> sigData;
    private Integer m;

    public List<String> getPubKeys() {
        return pubKeys;
    }

    public void setPubKeys(List<String> pubKeys) {
        this.pubKeys = pubKeys;
    }

    public List<String> getSigData() {
        return sigData;
    }

    public void setSigData(List<String> sigData) {
        this.sigData = sigData;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }
}
