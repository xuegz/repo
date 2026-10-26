package com.atoken.update.kusama.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.blockchain.utils.DateUtil;
import com.atoken.update.core.coin.account.BlockTxRecordCore;
import com.atoken.update.entity.DataDict;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.ExchangeService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.util.AtokenLogger;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.atoken.update.dot.core.DotTxRecordCore.*;

@Service
public class KsmBlockTxRecordCore extends BlockTxRecordCore implements AtokenLogger {

    {
        coinType = CoinEnum.ksm.name();
    }

    @Autowired
    private ExchangeService exchangeService;

    public KsmBlockTxRecordCore(@Qualifier("ksmSidecarBlockHandlerService") BlockHandlerService blockHandlerService,@Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig,@Autowired ExchangeService exchangeService) {
        super(blockHandlerService, blockchainConfig, exchangeService);
    }

    @Override
    public List<TxRecord> pullTxRecordData(String addr) {

        if (StringUtils.isEmpty(addr)){
            return null;
        }
        //获取ksm的api地址
        DataDict dataDict = exchangeService.selectDataDictByKey(CoinEnum.ksm.name());
        String apiPrefix = null,txRecordReq = null, txDetailReq = null;
        List<TxRecord> txRecordList = Lists.newArrayList();
        //ksm位数
        BigDecimal ksmPow = new BigDecimal(Math.pow(10,CoinEnum.ksm.decimals()));

        //区块hash请求
        AtomicReference<String> blockReq = new AtomicReference<>();
        //请求组装
        if (Objects.nonNull(dataDict) && StringUtils.isNotEmpty(dataDict.getValue())){
            apiPrefix = dataDict.getValue();
            List<String> preffixList = Arrays.asList(apiPrefix.split("/"));
            txRecordReq = preffixList.get(0)+"//"+ REQ_API_PREFIX +"."+preffixList.get(2)+API_VERSION+"/"+preffixList.get(3)+QUERY_ALL_TX_RECORD_API;
            txRecordReq = String.format(txRecordReq,addr);
            txDetailReq = preffixList.get(0)+"//"+ REQ_API_PREFIX +"."+preffixList.get(2)+API_VERSION+"/"+preffixList.get(3)+QUERY_TX_DETAIL_API;
            blockReq.set(preffixList.get(0)+"//"+ REQ_API_PREFIX +"."+preffixList.get(2)+API_VERSION+"/"+preffixList.get(3)+QUERY_BLOCK_HASH_API);
        }

        //请求交易记录
        AtomicReference<String> detailApiUrl = new AtomicReference<>();
        final String detailReq = txDetailReq;
        String txRecordResult = new Request().method(Method.GET).url(txRecordReq).send(String.class);
        JSONArray txRecordListArr = JSONObject.parseObject(txRecordResult).getJSONArray("data");
        if (txRecordListArr.size() == 0){
            logger.info("{} tx record is zero addr:{}",coinType,addr);
            return null;
        }

        txRecordListArr.stream().forEach(record->{
            JSONObject jsonObject = JSONObject.parseObject(record.toString()).getJSONObject("attributes");
            TxRecord txRecord = new TxRecord();
            txRecord.setBlockNumber(jsonObject.getLong("block_id"));
            txRecord.setBlockHash(getBlockHash(txRecord.getBlockNumber(),blockReq));
            txRecord.setNonce(jsonObject.getInteger("nonce"));
            String txHash = "0x"+jsonObject.getString("extrinsic_hash");
            txRecord.setHash(txHash);
            txRecord.setFrom(jsonObject.getString("address"));
            detailApiUrl.set(String.format(detailReq, txHash));
            JSONObject detailObject = getTxRecordDetail(txHash, detailApiUrl.get());
            txRecord.setTo(JSONObject.parseObject(detailObject.getJSONArray("params").get(0).toString()).getString("value"));
            String raw = "";
            BigDecimal value = BigDecimal.ZERO;
            if (detailObject.getJSONArray("params").size() > 1){
                raw = JSONObject.parseObject(detailObject.getJSONArray("params").get(1).toString()).getString("valueRaw");
                value = JSONObject.parseObject(detailObject.getJSONArray("params").get(1).toString()).getBigDecimal("value");
            }
            txRecord.setRaw(raw);
            txRecord.setValue(value.divide(ksmPow,CoinEnum.ksm.decimals(),BigDecimal.ROUND_HALF_DOWN));
            txRecord.setStatus(1);
            txRecord.setCreateTime(LocalDateTime.now());
            txRecord.setUpdateTime(LocalDateTime.now());
            txRecord.setTime(DateUtil.parse(detailObject.getString("datetime"),DateUtil.FORMAT_T1).getTime());
            txRecordList.add(txRecord);
        });

        return txRecordList;
    }

    private JSONObject getTxRecordDetail(String txHash,String reqApi) {
        String recordDetailResult = new Request().method(Method.GET).url(reqApi).send(String.class);
        return JSONObject.parseObject(recordDetailResult).getJSONObject("data").getJSONObject("attributes");
    }
}
