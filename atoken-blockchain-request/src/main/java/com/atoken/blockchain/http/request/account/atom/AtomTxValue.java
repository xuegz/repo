package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomTxValue
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomTxValue {
    private List<AtomMsg> msg;

    private AtomFee fee;

    private String memo;

    private List<AtomSignature> signatures;


    public void setSignatures(List<AtomSignature> signatures) {
        this.signatures = signatures;
    }

    public List<AtomSignature> getSignatures() {
        return this.signatures;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setFee(AtomFee fee) {
        this.fee = fee;
    }

    public AtomFee getFee() {
        return this.fee;
    }

    public void setMsg(List<AtomMsg> msg) {
        this.msg = msg;
    }

    public List<AtomMsg> getMsg() {
        return this.msg;
    }

}
