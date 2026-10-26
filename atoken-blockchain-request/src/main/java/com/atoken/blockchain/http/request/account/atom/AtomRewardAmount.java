package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

public class AtomRewardAmount {
    private List<AtomAmount> total;

    public List<AtomAmount> getTotal() {
        return total;
    }

    public AtomRewardAmount setTotal(List<AtomAmount> total) {
        this.total = total;
        return this;
    }
}
