package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomBlockId
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomBlockId {
    private AtomParts parts;

    private String hash;


    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public void setParts(AtomParts parts) {
        this.parts = parts;
    }

    public AtomParts getParts() {
        return this.parts;
    }

}
