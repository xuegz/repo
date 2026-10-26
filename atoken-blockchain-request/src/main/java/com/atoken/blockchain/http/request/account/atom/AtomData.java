package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

public class AtomData {
    private List<String> txs;

    public List<String> getTxs() {
        return txs;
    }

    public void setTxs(List<String> txs) {
        this.txs = txs;
    }
}
