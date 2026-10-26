package com.atoken.blockchain.http.request.account.atom;


import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomAmount
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomAmount {
    private BigDecimal amount = BigDecimal.ZERO;

    private String denom = "uatom";


    public void setDenom(String denom) {
        this.denom = denom;
    }

    public String getDenom() {
        return this.denom;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

}
