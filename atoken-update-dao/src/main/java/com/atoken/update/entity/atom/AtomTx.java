package com.atoken.update.entity.atom;

import com.atoken.update.entity.Tx;

public class AtomTx extends Tx {
    private String accountNum;

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }
}
