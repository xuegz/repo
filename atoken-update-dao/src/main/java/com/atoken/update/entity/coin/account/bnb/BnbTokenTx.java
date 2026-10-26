package com.atoken.update.entity.coin.account.bnb;

/**
 * @author atoken-team
 * @date 2021/02/24
 */
public class BnbTokenTx extends BnbTx {

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

    @Override
    public String toString() {
        return "BnbTokenTx{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", fromAddr='" + fromAddr + '\'' +
                ", toAddr='" + toAddr + '\'' +
                ", fee=" + fee +
                ", amount=" + amount +
                ", blockHash='" + blockHash + '\'' +
                ", blockHeight=" + blockHeight +
                ", blockState=" + blockState +
                ", timestamp=" + timestamp +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", state=" + state +
                ", assetShortName='" + assetShortName + '\'' +
                ", status=" + status +
                ", confirm=" + confirm +
                ", gasLimit=" + gasLimit +
                ", usedGasLimit=" + usedGasLimit +
                ", gasPrice=" + gasPrice +
                ", txId='" + txId + '\'' +
                ", type='" + type + '\'' +
                ", platType=" + platType +
                ", txNum=" + txNum +
                ", comment='" + comment + '\'' +
                ", feeType=" + feeType +
                ", nonce=" + nonce +
                ", mainCoin='" + mainCoin + '\'' +
                ", saveTx=" + saveTx +
                ", sendMessage=" + sendMessage +
                ", contractAddr='" + contractAddr + '\'' +
                ", decimals=" + decimals +
                '}';
    }
}
