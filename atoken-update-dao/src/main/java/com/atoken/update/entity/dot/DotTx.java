package com.atoken.update.entity.dot;

import com.atoken.update.entity.Tx;

import java.math.BigDecimal;

/**
 * @author: erwin
 * @time: 2020/11/13 2:13 下午
 */
public class DotTx extends Tx {
    private BigDecimal tip;

    public BigDecimal getTip() {
        return tip;
    }

    public void setTip(BigDecimal tip) {
        this.tip = tip;
    }
}
