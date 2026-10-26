package com.atoken.update.xwc.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.update.core.coin.account.BlockTxRecordCore;
import com.atoken.update.entity.DataDict;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.ExchangeService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service("xwcBlockTxRecordCore")
public class XwcBlockTxRecordCore extends BlockTxRecordCore {

    /**
     * 查询总条数api
     */
    private static final String QUERY_TOTAL_API_REQ_PREFIX = "/api/user_tokens/%s";

    /**
     * 查询交易记录api
     */
    private static final String QUERY_DETAIL_API_REQ_PREFIX = "/api/queryTrxByAddr";

    /**
     * 通过高度查询区块
     */
    private static final String QUERY_BLOCK_API_REQ_PREFIX = "/api/queryBlockTxNum";

    {
        coinType = CoinEnum.xwc.name();
    }

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    public XwcBlockTxRecordCore(@Qualifier("xwcBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig, @Autowired ExchangeService exchangeService) {
        super(blockHandlerService, blockchainConfig, exchangeService);
    }

    @Override
    public List<TxRecord> pullTxRecordData(String addr) {
        log.info("coin {} pull tx record param:{}", coinType, addr);
        //获取xwc的api地址
        DataDict dataDict = exchangeService.selectDataDictByKey(CoinEnum.xwc.name());
        String apiPrefix = null;
        if (Objects.nonNull(dataDict) && StringUtils.isNotEmpty(dataDict.getValue())) {
            apiPrefix = dataDict.getValue();
            apiPrefix = apiPrefix.substring(0, apiPrefix.indexOf("?"));
        }

        //先拉取地址的总交易数
        String totalData = new Request().url(apiPrefix.concat(String.format(QUERY_TOTAL_API_REQ_PREFIX, addr))).method(Method.GET).setContentType(ContentType.JSON_UTF8).send(String.class);
        Integer total = 0;

        if (StringUtils.isEmpty(totalData)) {
            return null;
        }

        try {
            Integer retCode = JSONObject.parseObject(totalData).getInteger("retCode");

            if (retCode != 200) {
                return null;
            }

            JSONObject xwcBalanceJsonObject = JSONObject.parseObject(totalData).getJSONObject("data").getJSONObject("xwcBanlance");
            if (Objects.isNull(xwcBalanceJsonObject)) {
                return null;
            }
            total = xwcBalanceJsonObject.getInteger("transaction");
        } catch (Exception e) {
            log.error("get xwc total transaction interface error:{}", addr, e);
            return null;
        }

        if (total == 0) {
            return null;
        }

        //拉取所有交易记录
        Map<String, Object> params = Maps.newHashMap();
        params.put("address", addr);
        params.put("page", "1");
        params.put("rows", total.toString());
        String totalTxRecord = new Request().url(apiPrefix.concat(QUERY_DETAIL_API_REQ_PREFIX)).method(Method.POST).setContentType(ContentType.JSON_UTF8).data(params).send(String.class);
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONObject.parseObject(totalTxRecord).getJSONObject("data").getJSONArray("rows");
        } catch (Exception e) {
            log.error("get xwc transaction record interface error:{}", e);
            return null;
        }

        AtomicReference<String> blockHashApi = new AtomicReference<>();
        blockHashApi.set(apiPrefix.concat(QUERY_BLOCK_API_REQ_PREFIX));
        //获取列表此处肯定只要能查到肯定有值
        List<TxRecord> txRecordList = Lists.newArrayList();
        BigDecimal xwcPow = new BigDecimal(Math.pow(10, CoinEnum.xwc.decimals().doubleValue()));
        jsonArray.stream().forEach(j -> {
            JSONObject jsonObject = JSONObject.parseObject(j.toString());
            TxRecord txRecord = new TxRecord();
            txRecord.setHash(jsonObject.getString("trxId"));
            txRecord.setBlockNumber(jsonObject.getLong("blockNum"));
            txRecord.setBlockHash(getBlockHash(txRecord.getBlockNumber(), blockHashApi.get()));
            txRecord.setFee(jsonObject.getBigDecimal("fee").divide(xwcPow, CoinEnum.xwc.decimals(), BigDecimal.ROUND_HALF_DOWN));
            txRecord.setFrom(jsonObject.getString("fromAccount"));
            txRecord.setTo(jsonObject.getString("toAccount"));
            txRecord.setTime(jsonObject.getLong("trxTime"));
            txRecord.setStatus(1);
            txRecord.setRaw("");
            txRecord.setIsToken(0);
            txRecord.setValue(jsonObject.getBigDecimal("amount").divide(xwcPow, CoinEnum.xwc.decimals(), BigDecimal.ROUND_HALF_DOWN));
            txRecord.setCreateTime(LocalDateTime.now());
            txRecord.setUpdateTime(LocalDateTime.now());
            txRecordList.add(txRecord);
        });

        return txRecordList;
    }

    public String getBlockHash(Long block, String api) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("blockNum", block);
        params.put("page", 1);
        params.put("rows", 10);
        String hash = "";
        try {
            String result = new Request().url(api).method(Method.POST).setContentType(ContentType.JSON_UTF8).data(params).send(String.class);
            JSONObject resultObj = JSONObject.parseObject(result);
            JSONObject dataObj = resultObj.getJSONObject("data");
            if (dataObj.isEmpty()) {
                return "";
            }

            JSONArray rows = dataObj.getJSONArray("rows");
            hash = rows.getJSONObject(0).getString("blockId");
        } catch (Exception e) {
            log.error("xwc tx record getBlockHash error:{}", e);
            return hash;
        }

        return hash;
    }

}
