package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/1 4:00 下午
 */
@Data
public class AccountStakingPayouts {
    private AtDTO at;
    private List<ErasPayouts> erasPayouts;
}