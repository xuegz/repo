package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2020/9/15 6:28 下午
 */
@Data
public class RuntimeSpecResp {


    private AtDTO at;
    private String authoringVersion;
    private String transactionVersion;
    private String implVersion;
    private String specName;
    private String specVersion;
    private ChainTypeDTO chainType;
    private PropertiesDTO properties;


}
