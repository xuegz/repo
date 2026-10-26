package com.atoken.update.entity.coin.account.bnb;

import java.math.BigDecimal;

/**
 * @author atoken-team
 * @date 2021/02/24
 */
public class BnbTokenBalance {

    private Long id;
    private String contractAddr;
    private String addr;
    private BigDecimal balance;
    private Integer decimals;
    private Byte added;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Byte getAdded() {
        return added;
    }

    public void setAdded(Byte added) {
        this.added = added;
    }
}
