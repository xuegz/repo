package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomOther
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomOther {
    private String txIndex;

    private String rpcAddress;


    public void setRpcAddress(String rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    public String getRpcAddress() {
        return this.rpcAddress;
    }

    public void setTxIndex(String txIndex) {
        this.txIndex = txIndex;
    }

    public String getTxIndex() {
        return this.txIndex;
    }

}
