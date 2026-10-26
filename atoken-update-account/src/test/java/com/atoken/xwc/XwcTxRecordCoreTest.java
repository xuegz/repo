package com.atoken.xwc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atoken.BaseTest;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.update.xwc.config.XwcBlockchainConfig;
import com.atoken.update.xwc.core.XwcBlockTxRecordCore;
import com.atoken.update.xwc.jobhandler.XwcTxRecordSyncJob;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class XwcTxRecordCoreTest extends BaseTest {

    private static final String QUERY_TOTAL_API_REQ_PREFIX = "/api/user_tokens/%s";
    private static final String QUERY_DETAIL_API_REQ_PREFIX = "/api/queryTrxByAddr";

    @Autowired
    private XwcBlockTxRecordCore xwcBlockTxRecordCore;

    @Autowired
    private XwcBlockchainConfig xwcBlockchainConfig;

    @Test
    public void test(){
        String apiPrefix = "https://explorer.whitecoin.info/";

        String addr = "XWCNdNe5vzqrS23VYqF2PmaP14HLj1ATfF5Pf";
        String totalData = new Request().url(apiPrefix.concat(String.format(QUERY_TOTAL_API_REQ_PREFIX, addr))).method(Method.GET).setContentType(ContentType.JSON_UTF8).send(String.class);
        Integer total = JSONObject.parseObject(totalData).getJSONObject("data").getJSONObject("xwcBanlance").getInteger("transaction");

        System.out.println(totalData);
        System.out.println(total);


        Map<String , String> params = Maps.newHashMap();
        params.put("address",addr);
        params.put("page","1");
        params.put("rows",total.toString());
        String totalTxRecord = new Request().url(apiPrefix.concat(QUERY_DETAIL_API_REQ_PREFIX)).method(Method.POST).setContentType(ContentType.JSON_UTF8).data(params).send(String.class);
        JSONArray jsonArray = JSONObject.parseObject(totalTxRecord).getJSONObject("data").getJSONArray("rows");
        System.out.println("size:"+jsonArray.size());
        System.out.println("交易记录:"+totalTxRecord);
    }

    @Test
    public void jobTest() throws Exception {
        System.out.println("任务执行开始");
        XwcTxRecordSyncJob xwcTxRecordSyncJob = new XwcTxRecordSyncJob(xwcBlockTxRecordCore,this.xwcBlockchainConfig);
        xwcTxRecordSyncJob.execute("");
        System.out.println("任务执行结束");
    }
}
