package com.atoken.update.entity.coin.account.ht;

import java.math.BigInteger;
import com.atoken.update.entity.coin.account.Tx;

/**
 * @author atoken-team
 * @date 2021/02/24
 */
public class HtTx extends Tx {

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
}
