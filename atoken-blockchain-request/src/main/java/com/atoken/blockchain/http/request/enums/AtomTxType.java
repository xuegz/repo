package com.atoken.blockchain.http.request.enums;

import java.util.stream.Stream;

public enum AtomTxType {
    MSGSEND("cosmos-sdk/MsgSend", "转账")
    , MSGWITHDRAWDELEGATIONREWARD("cosmos-sdk/MsgWithdrawDelegationReward", "赎回收益")
    , MSGUNDELEGATE("cosmos-sdk/MsgUndelegate", "赎回质押")
    , MSGDELEGATE("cosmos-sdk/MsgDelegate", "质押")
    , MSGWITHDRAWVALIDATORCOMMISSION("cosmos-sdk/MsgWithdrawValidatorCommission", "")
    , OTHER("other", "其他");

    private String type;
    private String description;
    AtomTxType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String description() {
        return description;
    }

    public static AtomTxType atomTxType(String type) {
        try{
            return Stream.of(AtomTxType.values())
                    .filter(value -> value.type.equals(type))
                    .findFirst().get();
        }catch (Exception e) {
            return AtomTxType.OTHER;
        }
    }

    public String type() {
        return type;
    }
}
