package com.atoken.blockchain.http.request.utxo.btcmodel;

import java.math.BigDecimal;

public class VOut<S extends ScriptPubKey> {
    protected BigDecimal valueSat;
    protected BigDecimal value;
    protected Integer n;
    protected S scriptPubKey;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public S getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(S scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public BigDecimal getValueSat() {
        return valueSat;
    }

    public void setValueSat(BigDecimal valueSat) {
        this.valueSat = valueSat;
    }
}
