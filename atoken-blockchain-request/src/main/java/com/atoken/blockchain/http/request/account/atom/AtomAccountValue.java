package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomAccountValue
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomAccountValue {
    private AtomPubKey publicKey;

    private String accountNumber;

    private Integer sequence;

    private String address;

    private List<AtomAmount> coins;


    public void setCoins(List<AtomAmount> coins) {
        this.coins = coins;
    }

    public List<AtomAmount> getCoins() {
        return this.coins;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getSequence() {
        return this.sequence;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setPublicKey(AtomPubKey publicKey) {
        this.publicKey = publicKey;
    }

    public AtomPubKey getPublicKey() {
        return this.publicKey;
    }

}
