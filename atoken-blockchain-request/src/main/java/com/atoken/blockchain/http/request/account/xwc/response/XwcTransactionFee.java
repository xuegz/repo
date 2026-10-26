package com.atoken.blockchain.http.request.account.xwc.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author legends_bai
 * @date 2020/12/8 下午6:05
 */
@Data
public class XwcTransactionFee {

    private BigDecimal amount;
    @JsonProperty("asset_id")
    private String assetId;
}
