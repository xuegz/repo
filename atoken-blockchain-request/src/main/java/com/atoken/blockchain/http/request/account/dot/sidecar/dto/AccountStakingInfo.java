package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 3:51 下午
 */
@Data
public class AccountStakingInfo {
    private Staking staking;
    private String controller;
    private At at;
    private Rewarddestination rewardDestination;
    private String numSlashingSpans;
}
