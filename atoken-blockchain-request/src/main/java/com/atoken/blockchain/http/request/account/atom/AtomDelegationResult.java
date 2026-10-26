package com.atoken.blockchain.http.request.account.atom;

public class AtomDelegationResult {

    private AtomDelegation delegation;

    private AtomDelegationBalance balance;

    public AtomDelegation getDelegation() {
        return delegation;
    }

    public void setDelegation(AtomDelegation delegation) {
        this.delegation = delegation;
    }

    public AtomDelegationBalance getBalance() {
        return balance;
    }

    public void setBalance(AtomDelegationBalance balance) {
        this.balance = balance;
    }
}
