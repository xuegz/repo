package com.atoken.update.entity.coin.account.ht;

/**
 * @author atoken-team
 * @date 2021/02/24
 */
public class HtTokenTx extends HtTx {

    private String contractAddr;

    private Integer decimals;

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }
}
