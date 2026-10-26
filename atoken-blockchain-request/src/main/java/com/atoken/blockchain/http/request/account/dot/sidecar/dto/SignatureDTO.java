package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 * @author: erwin
 * @time: 2020/9/9 3:08 下午
 */
@Data
public class SignatureDTO {

    private String signature;

    private String signer;
}
