package com.atoken.blockchain.http.request.enums;

public enum AtomCoin {
    uatom, atom, undefined;

    public boolean isCoin(String atomCoin) {
        return this.name().equals(atomCoin);
    }
}
