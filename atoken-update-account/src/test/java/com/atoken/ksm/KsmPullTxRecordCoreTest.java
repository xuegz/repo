package com.atoken.ksm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atoken.BaseTest;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.blockchain.utils.DateUtil;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.kusama.core.KsmBlockTxRecordCore;
import com.atoken.update.kusama.jobhandler.KsmTxRecordSyncJob;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class KsmPullTxRecordCoreTest extends BaseTest {

    @Autowired
    private KsmBlockTxRecordCore ksmBlockTxRecordCore;

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    @Test
    public void txRecord(){
        String apiPreffix = "https://polkascan.io/kusama/transaction/";

        //查询交易详情
//        String apiUrl = "/api/v1/balances/transfer?filter[address]=%s";

        //查询所有交易记录
        String apiUrl = "/api/v1/extrinsic?filter[address]=%s&page[size]=";

        //查询交易详情
        String extrinsicRecordDetailUrl = "/api/v1/extrinsic/%s";

        String apiUrlPrefix = "explorer-31";

        String addr = "J9SWKXnkVdpz9wuUnKyzm7JSHF3cGPePNZzUkxFtp9xnZy8";

        List<String> preffixList = Arrays.asList(apiPreffix.split("/"));
        apiUrl = preffixList.get(0)+"//"+ apiUrlPrefix+"."+preffixList.get(2)+"/"+preffixList.get(3)+apiUrl;
        extrinsicRecordDetailUrl = preffixList.get(0)+"//"+ apiUrlPrefix+"."+preffixList.get(2)+"/"+preffixList.get(3)+extrinsicRecordDetailUrl;
        System.out.println(apiUrl);

        String reqApiUrl = String.format(apiUrl,addr);
        AtomicReference<String> detailApiUrl = new AtomicReference<>();
        //查询所有交易
        String txRecordResult = new Request().method(Method.GET).url(reqApiUrl).send(String.class);
        JSONArray txRecordListArr = JSONObject.parseObject(txRecordResult).getJSONArray("data");
        List<TxRecord> txRecordList = Lists.newArrayList();
//        String finalExtrinsicRecordDetailUrl = extrinsicRecordDetailUrl;
        String aurl = extrinsicRecordDetailUrl;
        BigDecimal ksmPow = new BigDecimal(Math.pow(10,CoinEnum.ksm.decimals()));
        txRecordListArr.stream().forEach(recordList->{
            JSONObject jsonObject = JSONObject.parseObject(recordList.toString()).getJSONObject("attributes");
            TxRecord txRecord = new TxRecord();
            txRecord.setBlockNumber(jsonObject.getLong("block_id"));
            txRecord.setNonce(jsonObject.getInteger("nonce"));
            String txHash = "0x"+jsonObject.getString("extrinsic_hash");
            txRecord.setHash(txHash);
            txRecord.setFrom(jsonObject.getString("address"));
            detailApiUrl.set(String.format(aurl, txHash));
            JSONObject detailObject = getTxRecordDetail(txHash, detailApiUrl.get());
            txRecord.setTo(JSONObject.parseObject(detailObject.getJSONArray("params").get(0).toString()).getString("value"));
            txRecord.setRaw(JSONObject.parseObject(detailObject.getJSONArray("params").get(1).toString()).getString("valueRaw"));
            BigDecimal value = JSONObject.parseObject(detailObject.getJSONArray("params").get(1).toString()).getBigDecimal("value");
            txRecord.setValue(value.divide(ksmPow,CoinEnum.ksm.decimals(),BigDecimal.ROUND_HALF_DOWN));
            txRecord.setStatus(1);
            txRecord.setTime(DateUtil.parse(detailObject.getString("datetime"),DateUtil.FORMAT_T1).getTime());
            txRecordList.add(txRecord);
        });

        System.out.println(JSONObject.toJSONString(txRecordList));
    }

    @Test
    public void test2() throws ParseException {
        String time = "2021-01-26T02:40:54+00:00";
        System.out.println(DateUtil.parse(time,DateUtil.FORMAT_T1).getTime());
        getTxRecordDetail("0x9c338c1b0b1ee786e1aadb0d2f6c29441de9a9eb3439bae206bb8a471ff8ebd1","https://explorer-31.polkascan.io/kusama/api/v1/extrinsic/0x9c338c1b0b1ee786e1aadb0d2f6c29441de9a9eb3439bae206bb8a471ff8ebd1");

        BigDecimal a1 = new BigDecimal("119800000954");
        BigDecimal pow = new BigDecimal(Math.pow(10,CoinEnum.ksm.decimals()));
        BigDecimal a2 = a1.divide(pow,CoinEnum.ksm.decimals(),BigDecimal.ROUND_HALF_DOWN);
        System.out.println(a2);
    }


    private static JSONObject getTxRecordDetail(String txHash,String reqUrl){
        String recordDetail = new Request().method(Method.GET).url(reqUrl).send(String.class);
        JSONObject jsonObject = JSONObject.parseObject(recordDetail).getJSONObject("data").getJSONObject("attributes");
        return jsonObject;
    }

    @Test
    public void ksmTxRecordCoreTest() throws Exception {
        System.out.println("====任务执行开始");
        KsmTxRecordSyncJob ksmTxRecordSyncJob = new KsmTxRecordSyncJob(this.ksmBlockTxRecordCore,this.ksmBlockchainConfig);
        ksmTxRecordSyncJob.execute("");
        System.out.println("====任务执行结束");
    }
}
