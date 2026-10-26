package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: erwin
 * @time: 2020/9/9 2:41 下午
 */
@Data
public class BalanceInfoResp {

    private AtDTO at;

    private Integer nonce;

    private BigDecimal free;

    private Integer reserved;

    private BigDecimal miscFrozen;

    private BigDecimal feeFrozen;

    private List<LockDTO> locks;

}
