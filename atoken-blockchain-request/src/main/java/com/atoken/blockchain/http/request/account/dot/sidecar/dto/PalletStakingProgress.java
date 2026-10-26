package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/1 2:52 下午
 */
@Data
public class PalletStakingProgress {
    private String activeEra;
    private ElectionStatus electionStatus;
    private AtDTO at;
    private List<Object> unappliedSlashes;
    private String idealValidatorCount;
    private String nextActiveEraEstimate;
    private String nextSessionEstimate;
    private List<String> validatorSet;
    private String forceEra;
}