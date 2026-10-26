package com.atoken.blockchain.http.request.account.xwc.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.polkadot.types.type.Amount;

import java.util.Date;
import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/8 下午6:04
 */
@Data
public class XwcOperations {
    @JsonProperty("fee")
    private XwcTransactionFee fee;
    private String from;
    private String to;
    @JsonProperty("from_addr")
    private String fromAddr;
    @JsonProperty("to_addr")
    private String toAddr;
    @JsonProperty("amount")
    private XwcOperationsAmount amount;
    private List<String> extensions;

}
