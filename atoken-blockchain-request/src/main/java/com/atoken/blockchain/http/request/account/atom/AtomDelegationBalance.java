package com.atoken.blockchain.http.request.account.atom;

public class AtomDelegationBalance {

    private String denom;

    private String amount;

    public String getDenom() {
        return denom;
    }

    public void setDenom(String denom) {
        this.denom = denom;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
