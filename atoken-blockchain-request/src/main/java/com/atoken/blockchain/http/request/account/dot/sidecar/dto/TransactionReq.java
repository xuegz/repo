package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: erwin
 * @time: 2020/9/9 2:52 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReq {

    private String tx;
}
