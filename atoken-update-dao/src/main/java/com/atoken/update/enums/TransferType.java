package com.atoken.update.enums;

/**
 * @author dfg
 */
public enum TransferType {
    // 支出
    PAY(2)
    // 收入
    , INCOME(1);

    private Integer type;

    TransferType(Integer type) {
        this.type = type;
    }

    public Integer type() {
        return type;
    }
}
