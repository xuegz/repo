package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;


/**
 * @author: erwin
 * @time: 2020/9/16 1:53 下午
 */
@Data
public class TransactionMaterialResp {

    private AtDTO at;
    private String genesisHash;
    private String chainName;
    private String specName;
    private String specVersion;
    private String txVersion;
    private String metadata;
}
