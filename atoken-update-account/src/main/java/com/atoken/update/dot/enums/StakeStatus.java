package com.atoken.update.dot.enums;

public enum StakeStatus {
    NO_STAKE("无质押")
    , STAKING("质押中")
    , STAKED("质押完成")
    , CANCEL_STAKE("取消质押中")
    , UPDATE_STAKE("质押修改中");

    private String description;

    StakeStatus(String description) {
        this.description = description;
    }
}