package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomSignature
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomSignature {
    private String signature;

    private AtomPubKey pubKey;


    public void setPubKey(AtomPubKey pubKey) {
        this.pubKey = pubKey;
    }

    public AtomPubKey getPubKey() {
        return this.pubKey;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return this.signature;
    }

}
