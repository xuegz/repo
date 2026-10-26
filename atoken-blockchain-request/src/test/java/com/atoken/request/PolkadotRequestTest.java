package com.atoken.request;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.RpcParams;
import com.atoken.blockchain.http.request.RpcResponse;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import org.junit.Test;
import org.polkadot.api.promise.ApiPromise;
import org.polkadot.rpc.provider.http.HttpProvider;
import org.polkadot.types.type.Extrinsic;

import java.util.ArrayList;
import java.util.List;

public class PolkadotRequestTest {
    private HttpProvider httpProvider = new HttpProvider("http://172.17.3.54:9933");
    //    private WsProvider wsProvider = new WsProvider("wss://poc3-rpc.polkadot.io/");
    private ApiPromise api;

    @Test
    public void testPolkadot() {
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("chain_getBlockHash");
        RpcResponse.StringResponse result = new Request().url("http://example.com:9933").setContentType(ContentType.JSON_UTF8).method(Method.POST).data(rpcParams).send(RpcResponse.StringResponse.class);
        System.out.println("JSON.toJSONString(result) = " + JSON.toJSONString(result));

        RpcParams rpcParams1 = new RpcParams();
        rpcParams1.setMethod("chain_getHeader");
        List<Object> params = new ArrayList<>();
        params.add(result.getResult());
        rpcParams1.setParams(params);
        RpcResponse.StringResponse result2 = new Request().url("http://172.17.3.54:9933").setContentType(ContentType.JSON_UTF8).method(Method.POST).data(rpcParams1).send(RpcResponse.StringResponse.class);
        System.out.println("JSON.toJSONString(result2) = " + JSON.toJSONString(result2));


        RpcParams rpcParams3 = new RpcParams();
        rpcParams3.setMethod("chain_getBlock");
        List<Object> params1 = new ArrayList<>();
        params1.add("0xe79787e87a80943947a6e42e6b1491ad0b5820a6982a63c2e9ff4dbf8dcb3a25");
        rpcParams3.setParams(params1);
        RpcResponse.StringResponse result3 = new Request().url("http://172.17.3.54:9933").setContentType(ContentType.JSON_UTF8).method(Method.POST).data(rpcParams3).send(RpcResponse.StringResponse.class);
        System.out.println("JSON.toJSONString(result3) = " + JSON.toJSONString(result3));
    }

    @Test
    public void testHttpProvider() {
//        List<Object> params1 = new ArrayList<>();
//        params1.add("0xc5e2b1b9ff2bb90b1ad438939a8ca1d603227d29a8881f51269df7e614b7575f");
//        httpProvider.send("chain_getBlock", params1, null);

        HttpProvider httpProvider = new HttpProvider("http://172.17.3.54:9933");
//        Promise<ApiPromise> ready = ApiPromise.create(httpProvider);
        httpProvider.send("getHeader", null, null).then(result -> {
            System.out.println("result = " + result);
            return null;
        }).then(result -> {
            System.out.println("result = " + result);
            return null;
        });

    }
    
    @Test
    public void testParse() {
        Extrinsic extrinsic = new Extrinsic("0x81ffc22dd456c818697153c678f3b5fc3ab959f8cf2af3bb744a9a6903b13f9463707007e8cb982807dfe4d7572016800ec077f316390bc1ed453cb489e5f3c8e466941e62ce26d91bb7b211f73068a4f75532d2693419943adf75c9731f7ec9ae0d0845000500ff6b7f3c53de7612308eab0c07c8c188ae8c219c310684d81d1d8d595098b81c120b00c8b5ca2d9d00");
        System.out.println("extrinsic = " + extrinsic);
    }
}
