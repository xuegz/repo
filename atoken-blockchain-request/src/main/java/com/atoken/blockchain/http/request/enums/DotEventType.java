package com.atoken.blockchain.http.request.enums;

import java.util.Arrays;

public enum DotEventType {
    TRANSFER("0x0202", "交易")
    , OTHER("", "其他交易");
    private String typeCode;
    private String description;

    DotEventType(String typeCode, String description) {
        this.typeCode = typeCode;
        this.description = description;
    }

    public static DotEventType valueOfByTypeCode(String typeCode) {
        try {
            return Arrays.stream(DotEventType.values()).filter(dotEventType -> dotEventType.typeCode.equals(typeCode)).findFirst().get();
        } catch (Exception e) {
            return OTHER;
        }
    }
}
