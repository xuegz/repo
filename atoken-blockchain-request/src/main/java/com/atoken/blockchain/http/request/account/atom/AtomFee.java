package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomFee
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomFee {
    private List<AtomAmount> amount;

    private Integer gas;


    public void setGas(Integer gas) {
        this.gas = gas;
    }

    public Integer getGas() {
        return this.gas;
    }

    public void setAmount(List<AtomAmount> amount) {
        this.amount = amount;
    }

    public List<AtomAmount> getAmount() {
        return this.amount;
    }

}
