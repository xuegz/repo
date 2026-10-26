package com.atoken.blockchain.http.request.account.xwc.response;

import com.atoken.blockchain.http.request.RpcResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/8 下午7:16
 */
public class XwcAddressBalancesResponse {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetIdd) {
        this.assetId = assetIdd;
    }

    @JsonProperty("asset_id")
    private String assetId;


    public static class XwcAddressBalancesRpcResponse extends RpcResponse<List<XwcAddressBalancesResponse>> {
    }
}
