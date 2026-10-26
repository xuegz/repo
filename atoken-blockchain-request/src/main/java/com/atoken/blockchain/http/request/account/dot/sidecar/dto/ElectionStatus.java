package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 2:53 下午
 */
@Data
public class ElectionStatus {
    private String toggleEstimate;
    private Status status;
}
