package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/8 4:57 下午
 */
@Data
public class Ledger {

    private String total;
    private List<Object> unlocking;
    private String active;
    private String stash;
    private List<String> claimedRewards;

}