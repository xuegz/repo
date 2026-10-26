package com.atoken.update.core.ont;

import com.alibaba.fastjson.JSON;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.CoinFee;
import com.atoken.update.entity.ont.CurrentHeightDetail;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.ont.OntBlockHandlerService;
import com.atoken.update.service.ont.OntBlockService;
import com.atoken.update.service.ont.OntExchangeService;
import com.atoken.update.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class OntBlockInfoCore {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OntBlockHandlerService ontBlockHandlerService;
    @Autowired
    private OntBlockService blockService;
    @Value("${blockchain.block.number.limit: 10}")
    private Integer blockNumberLimit = 10;
    @Value("${blockchain.block.confirm: 10}")
    private Integer blockConfirm = 10;
    @Value("${blockchain.block.init.height: 1}")
    private Integer initBlockHeight;
    private String asset = "ont";

    @Value("${blockchain.asset}")
    private String blockchainAsset;
    @Value("${blockchain.coin-fee}")
    private boolean hasCoinFee;
    @Autowired
    private OntExchangeService ontExchangeService;
    @Autowired
    private DingdingNotification dingdingNotification;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;
    private static Long notificationSendTime = 0L;

    public void blockInfoHandler() {
        try{
            //获取链当前的高度
            Integer currentBlockNumber = ontBlockHandlerService.getBlockNumber();
            VerifyUtil.objectNotNull(currentBlockNumber, "get current block number failed");

            // 当前高度入redis
            CurrentHeightDetail currentHeightDetail = new CurrentHeightDetail();
            currentHeightDetail.setTheCurrentHeight(currentBlockNumber);
            currentHeightDetail.setToConfirmTheNumber(blockConfirm);
            stringRedisTemplate.boundHashOps(ConstantUtil.REDIS_KEY_BLOCK_DETAIL)
                    .put(blockchainAsset, JSON.toJSONString(currentHeightDetail));

            //获取数据库最大高度
            Integer dbMaxBlockNumber = blockService.selectMaxHeight();
            VerifyUtil.objectNotNull(dbMaxBlockNumber, "get db max block number failed");

            if (currentBlockNumber > dbMaxBlockNumber) {
                //得到本次获取多少个高度的信息
                Integer limitBlockNumber = currentBlockNumber - dbMaxBlockNumber > blockNumberLimit ? dbMaxBlockNumber + blockNumberLimit : currentBlockNumber;
                List<Integer> blockNumberList = DataUtil.generateLongList(dbMaxBlockNumber, limitBlockNumber);

                //获取高度信息blockhash
                List<Block> blockHashList = ontBlockHandlerService.getBlocks(blockNumberList);

                //将获取的blockhash存入数据库相映的表
                Integer rowNum = blockService.insertBlocks(blockHashList);
                VerifyUtil.objectNotNull(rowNum, "insert block hash failed");
                VerifyUtil.numberNotZore(rowNum, "insert block hash failed");
            } else if(hasCoinFee && currentBlockNumber.equals(dbMaxBlockNumber)) {
                // 获取当前区块信息
                List<Block> blockList = ontBlockHandlerService.getBlocks(Arrays.asList(currentBlockNumber));
                // 获取交易池交易数量
                Integer mempoolTxCount = ontBlockHandlerService.getMemPoolTxCount();
                // 更新coin fee表的数据
                CoinFee coinFee = new CoinFee();
                coinFee.setSymbol(blockchainAsset);
                coinFee.setBlockSize(blockList.get(0).getSize());
                coinFee.setBlockTxNum(blockList.get(0).getTxCount());
                coinFee.setMempoolTxNum(mempoolTxCount);
                ontExchangeService.updateCionFee(coinFee);
            }
        }catch(Exception e) {
            if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                dingdingNotification.blocksExceptionAlarm(asset, LoggerUtil.parseErrorStacks(e));
                notificationSendTime = Instant.now().getEpochSecond();
            }
            logger.error("update block error, exception:{}", e.getLocalizedMessage());
        }
    }

    @PostConstruct
    public void init() {
        logger.info("init block height start");
        //查看blockhash表是否有记录
        Integer count = blockService.selectCountBlock();
        VerifyUtil.objectNotNull(count, "init height failed");
        //没有记录，则初始化一条记录
        if(count <= 0) {
            //根据配置的高度，获取高度信息
            List<Block> blockHashList =ontBlockHandlerService.getBlocks(Arrays.asList(initBlockHeight));

            //将获取的高度信息入库
            Integer rowNum = blockService.insertBlocks(blockHashList);
            VerifyUtil.objectNotNull(rowNum, "insert block hash failed");
            VerifyUtil.numberNotZore(rowNum, "insert block hash failed");
        }

        logger.info("init block height complete");

        if(hasCoinFee) {
            logger.info("init coin fee start");
            // 初始化coin fee信息
            // 查询当前库里的区块高度
            Integer currentBlockNumber = ontBlockHandlerService.getBlockNumber();
            // 获取当前区块信息
            List<Block> blockList = ontBlockHandlerService.getBlocks(Arrays.asList(currentBlockNumber));
            // 获取交易池交易数量
            Integer mempoolTxCount = ontBlockHandlerService.getMemPoolTxCount();
            // 更新coin fee表的数据
            CoinFee coinFee = new CoinFee();
            coinFee.setSymbol(blockchainAsset);
            coinFee.setBlockSize(blockList.get(0).getSize());
            coinFee.setRate(BigDecimal.ZERO);
            coinFee.setBlockTxNum(blockList.get(0).getTxCount());
            coinFee.setMempoolTxNum(mempoolTxCount);
            ontExchangeService.insertCoinFee(coinFee);
            logger.info("init coin fee complete");
        }
    }
}
