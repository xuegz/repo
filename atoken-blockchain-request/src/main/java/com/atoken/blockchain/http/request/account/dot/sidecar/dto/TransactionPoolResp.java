package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2020/9/9 4:15 下午
 */
@Data
public class TransactionPoolResp {
    private List<Object> pool;
}
