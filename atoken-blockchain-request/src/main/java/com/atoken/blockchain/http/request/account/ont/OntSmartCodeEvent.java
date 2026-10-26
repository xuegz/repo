package com.atoken.blockchain.http.request.account.ont;

import java.math.BigDecimal;
import java.util.List;

public class OntSmartCodeEvent {
    private BigDecimal gasConsumed;
    private List<OntNotify> notify;
    private String txHash;
    private Integer state;

    public BigDecimal getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(BigDecimal gasConsumed) {
        this.gasConsumed = gasConsumed;
    }

    public List<OntNotify> getNotify() {
        return notify;
    }

    public void setNotify(List<OntNotify> notify) {
        this.notify = notify;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}