package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 2:28 下午
 */
@Data
public class AccountVestingInfo {

    private AtDTO at;

    private VestingSchedule vesting;

}
