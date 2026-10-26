package com.atoken.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.account.xwc.XwcClient;
import com.atoken.blockchain.http.request.account.xwc.response.*;
import com.atoken.blockchain.utils.DataUtil;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author legends_bai
 * @date 2020/12/9 上午10:06
 */
public class XwcRequestTest {

    private XwcClient xwcClient = new XwcClient("http://154.209.69.120", "23457");

    @Test
    public void testGetInfo() {
        XwcInfoResponse xwcInfoResponse = xwcClient.getInfo();
        System.out.println("xwcInfoResponse = " + xwcInfoResponse.getChainId());
    }
    @Test
    public void testGetBlock() {
        XwcBlockResponse xwcBlockResponse = xwcClient.getBlock(1100);
        System.out.println("xwcInfoResponse = " + xwcBlockResponse.getBlockId());
    }
    @Test
    public void testGetTransaction() {
        XwcTransactionResponse xwcTransactionResponse = xwcClient.getTransaction("993957b2670894ff252a3d7f999c998a799ade15");
        List<Object> objects = xwcTransactionResponse.getOperations().stream().filter(Objects::nonNull).findFirst().get();
        XwcOperations xwcOperations = JSONObject.toJavaObject((JSON) JSON.toJSON(objects.get(1)), XwcOperations.class);
        System.out.println("xwcInfoResponse = " + xwcOperations.getFrom());
    }
    @Test
    public void testGetAddrBalances() {
        List<XwcAddressBalancesResponse> xwcAddressBalancesResponse = xwcClient.getAddrBalances("XWCNRkWYWkdvRkTj1SULfyJxHq5wfk5UaYt7q");
        xwcAddressBalancesResponse.stream().forEach(e-> {
            System.out.println(DataUtil.formatAmountByDecimal(e.getAmount(),8));

        });
    }
    @Test
    public void testLightwalletBroadcast() {
        JsonObject jsonObject = new JsonObject();
        String aa = "{\"ref_block_num\":23677,\"ref_block_prefix\":578269491,\"expiration\":\"2020-12-11T05:55:49\",\"operations\":[[0,{\"fee\":{\"amount\":2000000,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.0\",\"to\":\"1.2.0\",\"from_addr\":\"XWCNL7S98YDUaZTZAmWBHbkZcaNf1yrfU8e8A\",\"to_addr\":\"XWCNL7S98YDUaZTZAmWBHbkZcaNf1yrfU8e8A\",\"amount\":{\"amount\":100000000,\"asset_id\":\"1.3.0\"},\"extensions\":[]}]],\"extensions\":[],\"signatures\":[\"1f34b1889231b8a6a87942e07c4b6a51f80fecebf91dacd1fd1f48d58918a3cb275bf209de8cb2b2d8478d93baf1836f5da8a414df3c3d1d0d67cfbceb02393d25\"]}";
        JSONObject o = (JSONObject) JSONObject.parse(aa);
        System.out.println(o);
        String s = xwcClient.lightwalletBroadcast(o);
        System.out.println(s);
    }
}
