package com.atoken.blockchain.http.request.account.xwc;

import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.RequestClient;
import com.atoken.blockchain.http.request.RpcParams;
import com.atoken.blockchain.http.request.account.xwc.response.*;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/8 下午4:27
 * @Desc 白币请求客户端
 */
public class XwcClient extends RequestClient {

    private String rpcPort;

    public XwcClient(String baseUrl, String rpcPort) {
        super(baseUrl);
        this.rpcPort = rpcPort;
    }

    /**
     * 查询最新块高
     *
     * @return
     */
    public XwcInfoResponse getInfo() {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("info");
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(XwcInfoResponse.XwcInfoRpcResponse.class)
                .getResult();
    }

    /**
     * 根据块高查询交易信息
     *
     * @return
     */
    public XwcBlockResponse getBlock(Integer blockHeight) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("get_block");
        List<Object> list = Lists.newArrayList();
        list.add(blockHeight);
        rpcParams.setParams(list);
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(XwcBlockResponse.XwcBlockRpcResponse.class)
                .getResult();
    }

    /**
     * 根据hash 查询交易信息
     *
     * @return
     */
    public XwcTransactionResponse getTransaction(String tx) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("get_transaction");
        List<Object> list = Lists.newArrayList();
        list.add(tx);
        rpcParams.setParams(list);
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(XwcTransactionResponse.XwcTransactionRpcResponse.class)
                .getResult();
    }

    /**
     * 交易发送
     *
     * @return
     */
    public String lightwalletBroadcast(JSONObject jsonObject) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("lightwallet_broadcast");
        List<Object> list = Lists.newArrayList();
        list.add(jsonObject);
        rpcParams.setParams(list);
        XwcLightWalletBroadcastResponse send = request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(XwcLightWalletBroadcastResponse.class);
        if(StringUtils.isBlank(send.getResult())){
            return JSONObject.toJSONString(send.getError());
        }
        return send.getResult();
    }

    /**
     * 查询地址余额
     *
     * @return
     */
    public List<XwcAddressBalancesResponse> getAddrBalances(String address) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("get_addr_balances");
        List<Object> list = Lists.newArrayList();
        list.add(address);
        rpcParams.setParams(list);
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(XwcAddressBalancesResponse.XwcAddressBalancesRpcResponse.class)
                .getResult();
    }
}
