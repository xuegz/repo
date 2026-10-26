package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2020/9/9 2:56 下午
 */
@Data
public class BlockResp {

    private Integer number;
    private String hash;
    private String parentHash;
    private String stateRoot;
    private String extrinsicsRoot;
    private String authorId;

    private List<LogsDTO> logs;

    private OnInitializeDTO onInitialize;

    private List<ExtrinsicsDTO> extrinsics;

    private OnFinalizeDTO onFinalize;

}
