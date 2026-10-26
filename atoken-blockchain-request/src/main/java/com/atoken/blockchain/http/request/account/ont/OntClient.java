package com.atoken.blockchain.http.request.account.ont;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.exception.BalanceNotEnoughException;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.RequestClient;
import com.atoken.blockchain.http.request.RpcParams;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import com.github.ontio.OntSdk;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.network.exception.RpcException;
import com.github.ontio.sdk.exception.SDKException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OntClient extends RequestClient {
    private OntSdk ontSdk;

    static {
        System.setProperty("sun.net.client.defaultConnectTimeout", "100000");
        System.setProperty("sun.net.client.defaultReadTimeout", "100000");
    }

    public OntClient(String baseUrl) {
        super(baseUrl);
        ontSdk = OntSdk.getInstance();
        ontSdk.setRpc(baseUrl);
        try {
            ontSdk.setDefaultConnect(ontSdk.getRpc());
        } catch (SDKException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getBlockHeight() {
        Integer blockHeight;
        try {
            blockHeight = ontSdk.getRpc().getBlockHeight();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return blockHeight;
    }

    public OntBlock getBlockByHeight(Integer blockHeight) {
        Object block;
        try {
            block = ontSdk.getRpc().getBlockJson(blockHeight);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        OntBlock ontBlock = JSON.parseObject(JSON.toJSONString(block), OntBlock.class);
        return ontBlock;
    }

    public String getBlockHashByHeight(Integer blockHeight) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("getblockhash");
        rpcParams.setParams(Arrays.asList(blockHeight));
        return request.url(baseUrl)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(OntRpcResponse.StringResponse.class)
                .getResult();
    }

    public OntContractList getOntContractList(String url, String tokenType, Integer pageSize, Integer pageNum) {
        return new Request().url(url, tokenType, pageSize.toString(), pageNum.toString()).method(Method.GET).send(OntApiResponse.OntContractListResponse.class).getResult();
    }

    public Map<String, BigDecimal> getBalanceByAddress(String address) {
        Map<String, BigDecimal> tokenBalance = new HashMap<>();
        try {
            JSONObject jsonRepsonse = (JSONObject) ontSdk.getRpc().getBalance(address);
            jsonRepsonse.keySet().forEach(key -> tokenBalance.put(key, jsonRepsonse.getBigDecimal(key)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tokenBalance;
    }

    public List<OntSmartCodeEvent> getSmartCodeEvent(Integer height) {
        List<OntSmartCodeEvent> smartCodeEvents;
        try {
            JSONArray jsonRepsonse = (JSONArray) ontSdk.getRpc().getSmartCodeEvent(height);
            smartCodeEvents = JSON.parseArray(JSON.toJSONString(jsonRepsonse), OntSmartCodeEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return smartCodeEvents;
    }

    public BigDecimal getUnbindOng(String address) {
        try {
            return new BigDecimal(ontSdk.getRpc().getGrantOng(address));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal getExtractableOng(String address) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("getunboundong");
        rpcParams.setParams(Arrays.asList(address));
        return request.url(baseUrl)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(OntRpcResponse.BigDecimalResponse.class)
                .getResult();
    }

    public OntPreTransaction preExecuteTx(String txDate) {
        try {
            Object obj = ontSdk.getRpc().sendRawTransactionPreExec(txDate);
            return JSON.parseObject(JSON.toJSONString(obj), OntPreTransaction.class);
        } catch (RpcException e) {
            if(e.getMessage().indexOf("balance insufficient") >= 0) {
                throw new BalanceNotEnoughException(e);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getMemPoolTxCount() {
        try {
            Object obj = ontSdk.getRpc().getMemPoolTxCount();
            return JSON.parseArray(JSON.toJSONString(obj), Integer.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
