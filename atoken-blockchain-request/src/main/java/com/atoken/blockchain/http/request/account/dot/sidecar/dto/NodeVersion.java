package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2021/4/1 2:48 下午
 */
@Data
public class NodeVersion {
    private String clientImplName;
    private String chain;
    private String clientVersion;
}
