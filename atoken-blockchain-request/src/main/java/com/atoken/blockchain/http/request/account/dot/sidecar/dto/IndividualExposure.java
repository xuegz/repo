package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author: erwin
 * @time: 2021/4/7 2:08 下午
 */
@Data
public class IndividualExposure {
    private String total;
    private Map<String, Integer> individual;
}
