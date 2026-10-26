package com.atoken.update.enums;

import java.math.BigDecimal;

/**
 * @author dfg
 */
public enum FeeType {
    // 通知的时候总金额加上fee，适用于当前币种和手续费币种一致的情况
    RECORD_FEE(BigDecimal.ONE)
    // 通知的时候总金额不加上fee，适用于当前币种和手续费币种不一致的情况
    , NO_RECORD_FEE(BigDecimal.ZERO);
    private BigDecimal rate;

    FeeType(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal rate() {
        return this.rate;
    }
}
