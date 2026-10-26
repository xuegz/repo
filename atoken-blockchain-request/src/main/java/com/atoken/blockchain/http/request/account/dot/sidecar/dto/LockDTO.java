package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: erwin
 * @time: 2020/9/9 3:36 下午
 */
@Data
public class LockDTO {


    private String id;

    private BigDecimal amount;

    private String reasons;


}
