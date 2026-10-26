package com.atoken.update.entity.coin.account.bnb;

import java.math.BigInteger;
import com.atoken.update.entity.coin.account.Tx;

/**
 * @author atoken-team
 * @date 2021/02/24
 */
public class BnbTx extends Tx {

    private String txHash;
    private BigInteger gas;
    private BigInteger gasUsed;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public BigInteger getGas() {
        return gas;
    }

    public void setGas(BigInteger gas) {
        this.gas = gas;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    @Override
    public String toString() {
        return "BnbTx{" +
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
                ", txHash='" + txHash + '\'' +
                ", gas=" + gas +
                ", gasUsed=" + gasUsed +
                '}';
    }
}
