package com.atoken.update.dot.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.blockchain.utils.DateUtil;
import com.atoken.update.core.BlockTxRecordCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.DataDict;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.ExchangeService;
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

@Service
public class DotTxRecordCore extends BlockTxRecordCore implements AtokenLogger {

    public static final String QUERY_ALL_TX_RECORD_API = "/extrinsic?filter[address]=%s&page[size]=";

    public static final String QUERY_TX_DETAIL_API = "/extrinsic/%s";

    public static final String QUERY_BLOCK_HASH_API = "/block/%s";

    public static final String REQ_API_PREFIX = "explorer-32";

    public static final String API_VERSION = "/api/v1";

    {
        coinType = CoinEnum.dot.name();
    }

    @Autowired
    private ExchangeService exchangeService;

    public DotTxRecordCore(@Qualifier("dotSidecarBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig, @Autowired ExchangeService exchangeService) {
        super(blockHandlerService, blockchainConfig, exchangeService);
    }

    @Override
    public List<TxRecord> pullTxRecordData(String addr) {
        if (StringUtils.isEmpty(addr)){
            return null;
        }
        //获取dot的api地址
        DataDict dataDict = exchangeService.selectDataDictByKey(CoinEnum.dot.name());
        String apiPrefix = null,txRecordReq = null;
        List<TxRecord> txRecordList = Lists.newArrayList();
        //dot位数
        BigDecimal dotPow = new BigDecimal(Math.pow(10,CoinEnum.dot.decimals()));

        AtomicReference<String> blockReq = new AtomicReference<>();
        AtomicReference<String> detailApiUrl = new AtomicReference<>();
        AtomicReference<String> txDetailReq = new AtomicReference<>();
        //请求组装
        if (Objects.nonNull(dataDict) && StringUtils.isNotEmpty(dataDict.getValue())){
            apiPrefix = dataDict.getValue();
            List<String> preffixList = Arrays.asList(apiPrefix.split("/"));
            txRecordReq = preffixList.get(0)+"//"+ REQ_API_PREFIX +"."+preffixList.get(2)+API_VERSION+"/polkadot"+QUERY_ALL_TX_RECORD_API;
            txRecordReq = String.format(txRecordReq,addr);
            txDetailReq.set(preffixList.get(0)+"//"+ REQ_API_PREFIX +"."+preffixList.get(2)+API_VERSION+"/polkadot"+QUERY_TX_DETAIL_API);
            blockReq.set(preffixList.get(0)+"//"+REQ_API_PREFIX+"."+preffixList.get(2)+API_VERSION+"/polkadot"+QUERY_BLOCK_HASH_API);
        }

        //请求交易记录
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
            txRecord.setNonce(jsonObject.getInteger("nonce"));
            txRecord.setBlockHash(getBlockHash(txRecord.getBlockNumber(),blockReq));
            String txHash = "0x"+jsonObject.getString("extrinsic_hash");
            txRecord.setHash(txHash);
            txRecord.setFrom(jsonObject.getString("address"));
            detailApiUrl.set(String.format(txDetailReq.get(), txHash));
            JSONObject detailObject = getTxRecordDetail(txHash, detailApiUrl.get());
            txRecord.setTo(JSONObject.parseObject(detailObject.getJSONArray("params").get(0).toString()).getString("value"));
            String raw = "";
            BigDecimal value = BigDecimal.ZERO;
            if (detailObject.getJSONArray("params").size() > 1){
                raw = JSONObject.parseObject(detailObject.getJSONArray("params").get(1).toString()).getString("valueRaw");
                value = JSONObject.parseObject(detailObject.getJSONArray("params").get(1).toString()).getBigDecimal("value");
            }
            txRecord.setRaw(raw);
            txRecord.setValue(value.divide(dotPow,CoinEnum.dot.decimals(),BigDecimal.ROUND_HALF_DOWN));
            txRecord.setStatus(1);
            txRecord.setCreateTime(LocalDateTime.now());
            txRecord.setUpdateTime(LocalDateTime.now());
            txRecord.setTime(DateUtil.parse(detailObject.getString("datetime"),DateUtil.FORMAT_T1).getTime());
            txRecordList.add(txRecord);
        });

        return txRecordList;
    }

    public static String getBlockHash(Long block,AtomicReference<String> reqApi){
        String api = String.format(reqApi.get(), block);
        String blockHashResult = new Request().method(Method.GET).url(api).send(String.class);
        JSONObject data = JSONObject.parseObject(blockHashResult).getJSONObject("data");
        if (data.isEmpty()){
            return "";
        }

        String hash = data.getJSONObject("attributes").getString("hash");
        return hash;
    }

    private JSONObject getTxRecordDetail(String txHash,String reqApi) {
        String recordDetailResult = new Request().method(Method.GET).url(reqApi).send(String.class);
        return JSONObject.parseObject(recordDetailResult).getJSONObject("data").getJSONObject("attributes");
    }
}
