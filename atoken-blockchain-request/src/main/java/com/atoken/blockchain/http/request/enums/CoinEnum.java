package com.atoken.blockchain.http.request.enums;

/**
 * Created by Administrator on 2017/10/11.
 */
public enum CoinEnum {
    btc(0, 1, 0, ""),
    bch(0, 1, 0, ""),
    xwc(8, 1, 0, ""),
    usdt(0, 1, 0, "6f6d6e69000000000000001f"),
    dash(0, 1, 0, ""),
    ltc(0, 1, 0, ""),
    sbtc(0, 1, 0, ""),
    sc(0, 1, 0, ""),
    lch(0, 1, 0, ""),
    zec(0, 1, 0, ""),
    nas(18, 1, 0, ""),
    eth(0, 1, 0, ""),
    bcd(0, 1, 0, ""),
    btm(8, 1, 0, ""),
    dali(0, 1, 0, ""),
    ut(0, 1, 0, ""),
    trx(6, 1, 0, ""),
    bu(8, 1, 0, ""),
    vet(18, 1, 0, ""),
    vtho(18, 1, 0, ""),
    pai(0, 1, 0, ""),
    ela(8, 1, 0, ""),
    atom(6, 1, 0, ""),
    dot(10, 1, 0, ""),
    ksm(12, 1, 0, ""),
    tomo(18, 1, 0, ""),
    ht(18, 1, 0, ""),
    bnb(18, 1, 0, ""),
    kar(12, 1, 0, "");

    private Integer decimals;
    private Integer syncState;
    private Integer depositOrder;
    private String info;

    CoinEnum(Integer decimals, Integer syncState, Integer depositOrder, String info) {
        this.decimals = decimals;
        this.syncState = syncState;
        this.depositOrder = depositOrder;
        this.info = info;
    }


    public Integer decimals() {
        return decimals;
    }

    public Integer syncState() {
        return syncState;
    }

    public Integer depositOrder() {
        return depositOrder;
    }

    public String info() {
        return this.info;
    }
}
