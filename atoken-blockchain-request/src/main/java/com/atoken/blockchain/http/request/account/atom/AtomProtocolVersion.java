package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomProtocolVersion
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomProtocolVersion {
    private String app;

    private String block;

    private String p2p;


    public void setP2p(String p2p) {
        this.p2p = p2p;
    }

    public String getP2p() {
        return this.p2p;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBlock() {
        return this.block;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getApp() {
        return this.app;
    }

}
