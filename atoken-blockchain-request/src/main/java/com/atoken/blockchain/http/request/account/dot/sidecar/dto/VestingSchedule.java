package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 2:27 下午
 */
@Data
public class VestingSchedule {

    private String locked;

    private String perBlock;

    private String startingBlock;
}
