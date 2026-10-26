package com.atoken.blockchain.http.request.account.dot;

import com.atoken.blockchain.http.request.RpcParams;

import java.util.Arrays;

public enum DotRpcMethod {

    //
    GET_BLOCK_HASH("chain_getBlockHash", "height"),
    GET_BLOCK_HEADER("chain_getHeader", "blockHash"),
    GET_BLOCK("chain_getBlock", "blockHash"),
    GET_STORAGE("state_getStorage", "storageKey", "hash"),
    GET_METADATA("state_getMetadata", "hash"),
    GET_QUERY_STORAGE("state_queryStorage", "storageKey", "hash"),
    GET_PENDING_EXTRINSICS("author_pendingExtrinsics"),
    SEND_TX("author_submitExtrinsic", "signedTxStr");
    private String method;
    private String[] params;

    DotRpcMethod(String method, String... params) {
        this.method = method;
        this.params = params;
    }

    public String method() {
        return this.method;
    }

    public String[] params() {
        return this.params;
    }

    public RpcParams obtainRpcParams(Object... params) {
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod(this.method);
        rpcParams.setParams(Arrays.asList(params));
        return rpcParams;
    }
}
