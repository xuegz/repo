package com.atoken.blockchain.http.request.account.atom;

import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomStakingPool
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomStakingPool {
    private BigDecimal notBondedTokens;

    private BigDecimal bondedTokens;


    public void setBondedTokens(BigDecimal bondedTokens) {
        this.bondedTokens = bondedTokens;
    }

    public BigDecimal getBondedTokens() {
        return this.bondedTokens;
    }

    public void setNotBondedTokens(BigDecimal notBondedTokens) {
        this.notBondedTokens = notBondedTokens;
    }

    public BigDecimal getNotBondedTokens() {
        return this.notBondedTokens;
    }

}
