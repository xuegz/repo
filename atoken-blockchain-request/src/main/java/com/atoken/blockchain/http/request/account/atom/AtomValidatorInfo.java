package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomValidatorInfo
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomValidatorInfo {
    private String address;

    private AtomPabKey pubKey;

    private String votingPower;


    public void setVotingPower(String votingPower) {
        this.votingPower = votingPower;
    }

    public String getVotingPower() {
        return this.votingPower;
    }

    public void setPubKey(AtomPabKey pubKey) {
        this.pubKey = pubKey;
    }

    public AtomPabKey getPubKey() {
        return this.pubKey;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

}
