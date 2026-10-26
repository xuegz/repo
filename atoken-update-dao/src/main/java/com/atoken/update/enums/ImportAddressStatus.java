package com.atoken.update.enums;

/**
 * @author dfg
 */
public enum ImportAddressStatus {
    // 未处理
    UNTREATED(0, "未处理")
    , TREATED(1, "已处理")
    , TREATED_FAIL(2, "处理失败");
    private Integer status;
    private String description;

    ImportAddressStatus(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer status() {
        return status;
    }
}
