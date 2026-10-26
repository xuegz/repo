package com.atoken.blockchain.http.request.account.xwc.response;
import com.atoken.blockchain.http.request.RpcResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/8 下午5:50
 */
public class XwcTransactionResponse {


    public Integer getRefBlockNum() {
        return refBlockNum;
    }

    public void setRefBlockNum(Integer refBlockNum) {
        this.refBlockNum = refBlockNum;
    }

    public BigInteger getRefBlockPrefix() {
        return refBlockPrefix;
    }

    public void setRefBlockPrefix(BigInteger refBlockPrefix) {
        this.refBlockPrefix = refBlockPrefix;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }


    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public List<String> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<String> signatures) {
        this.signatures = signatures;
    }

    public BigInteger getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(BigInteger blockNum) {
        this.blockNum = blockNum;
    }

    public String getTrxid() {
        return trxid;
    }

    public void setTrxid(String trxid) {
        this.trxid = trxid;
    }

    @JsonProperty("ref_block_num")
    private Integer refBlockNum;
    @JsonProperty("ref_block_prefix")
    private BigInteger refBlockPrefix;
    private Date expiration;

    public List<List<Object>> getOperations() {
        return operations;
    }

    public void setOperations(List<List<Object>> operations) {
        this.operations = operations;
    }

    private List<List<Object>> operations;
    private List<String> extensions;
    private List<String> signatures;
    @JsonProperty("block_num")
    private BigInteger blockNum;
    private String trxid;

    public static class XwcTransactionRpcResponse extends RpcResponse<XwcTransactionResponse> {
    }
    }

