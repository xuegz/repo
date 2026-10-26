package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.http.request.RpcResponse;

public class AtomMemPoolInfo {
    private Integer nTxs;
    private Integer total;
    private Integer totalBtyes;

    public Integer getnTxs() {
        return nTxs;
    }

    public void setnTxs(Integer nTxs) {
        this.nTxs = nTxs;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalBtyes() {
        return totalBtyes;
    }

    public void setTotalBtyes(Integer totalBtyes) {
        this.totalBtyes = totalBtyes;
    }

    public static class AtomMemPoolInfoResponse extends RpcResponse<AtomMemPoolInfo> {
    }
}
