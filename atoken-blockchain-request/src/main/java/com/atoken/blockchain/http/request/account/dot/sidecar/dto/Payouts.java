package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;


/**
 * @author: erwin
 * @time: 2021/4/1 4:01 下午
 */
@Data
public class Payouts {
    private String validatorCommission;
    private String totalValidatorExposure;
    private String nominatorExposure;
    private boolean claimed;
    private String validatorId;
    private String totalValidatorRewardPoints;
    private String nominatorStakingPayout;
}
