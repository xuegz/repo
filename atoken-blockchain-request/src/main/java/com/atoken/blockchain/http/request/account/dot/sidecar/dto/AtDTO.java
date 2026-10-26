package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author: erwin
 * @time: 2020/9/9 2:44 下午
 */
@Data
public class AtDTO {

    private String hash;

    private BigInteger height;
}
