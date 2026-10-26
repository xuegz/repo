package com.atoken.update.core;

import com.alibaba.fastjson.JSON;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.CoinFee;
import com.atoken.update.entity.CurrentHeightDetail;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.BlockService;
import com.atoken.update.service.ExchangeService;
import com.atoken.update.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public abstract class BlockInfoCore implements AtokenLogger {
    @Autowired
    private BlockService blockService;
    private BlockHandlerService blockHandlerService;
    private BlockchainConfig blockchainConfig;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private DingdingNotification dingdingNotification;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;
    private static Long notificationSendTime = 0L;

    public BlockInfoCore(BlockHandlerService blockHandlerService, BlockchainConfig blockchainConfig) {
        this.blockchainConfig = blockchainConfig;
        this.blockHandlerService = blockHandlerService;
    }


    public void blockInfoHandler() {
        try {
            //获取链当前的高度
            Integer currentBlockNumber = blockHandlerService.getBlockNumber();
            VerifyUtil.objectNotNull(currentBlockNumber, "get current block number failed");

            try {
                // 当前高度入redis
                CurrentHeightDetail currentHeightDetail = new CurrentHeightDetail();
                currentHeightDetail.setTheCurrentHeight(currentBlockNumber);
                currentHeightDetail.setToConfirmTheNumber(blockchainConfig.getBlockConfirm());
                stringRedisTemplate.boundHashOps(ConstantUtil.REDIS_KEY_BLOCK_DETAIL)
                        .put(blockchainConfig.getMainAsset(), JSON.toJSONString(currentHeightDetail));
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }

            //获取数据库最大高度
            Integer dbMaxBlockNumber = blockService.selectMaxHeight(blockchainConfig.getDataBase());
            VerifyUtil.objectNotNull(dbMaxBlockNumber, "get db max block number failed");

            if (currentBlockNumber - blockchainConfig.getDelayHeight() > dbMaxBlockNumber) {
                //得到本次获取多少个高度的信息
                Integer limitBlockNumber = currentBlockNumber - dbMaxBlockNumber > blockchainConfig.getBlockNumberLimit() ? dbMaxBlockNumber + blockchainConfig.getBlockNumberLimit() : currentBlockNumber;
                List<Integer> blockNumberList = DataUtil.generateLongList(dbMaxBlockNumber, limitBlockNumber);

                //获取高度信息blockhash
                List<Block> blockHashList = blockHandlerService.getBlocks(blockNumberList);

                //将获取的blockhash存入数据库相映的表
                Integer rowNum = blockService.insertBlocks(blockchainConfig.getDataBase(), blockHashList);
                VerifyUtil.objectNotNull(rowNum, "insert block hash failed");
                VerifyUtil.numberNotZore(rowNum, "insert block hash failed");
            } else if (blockchainConfig.isHasCoinFee() && currentBlockNumber - blockchainConfig.getDelayHeight() == dbMaxBlockNumber) {
                // 获取当前区块信息
                List<Block> blockList = blockHandlerService.getBlocks(Arrays.asList(currentBlockNumber));
                // 获取交易池交易数量
                Integer mempoolTxCount = blockHandlerService.getMemPoolTxCount();
                // 更新coin fee表的数据
                CoinFee coinFee = new CoinFee();
                coinFee.setSymbol(blockchainConfig.getMainAsset());
                coinFee.setBlockSize(blockList.get(0).getSize());
                coinFee.setBlockTxNum(blockList.get(0).getTxCount());
                coinFee.setMempoolTxNum(mempoolTxCount);
                exchangeService.updateCionFee(coinFee);
            }
        } catch (Exception e) {
            if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                dingdingNotification.blocksExceptionAlarm(blockchainConfig.getMainAsset(), LoggerUtil.parseErrorStacks(e));
                notificationSendTime = Instant.now().getEpochSecond();
            }
            logger.error("update block error, exception:{}", e.getLocalizedMessage());
        }
    }


    public void blockInfoHandler(List<Integer> blockNumberList) {
        try {
            //获取高度信息blockhash
            List<Block> blockHashList = blockHandlerService.getBlocks(blockNumberList);

            //将获取的blockhash存入数据库相映的表
            Integer rowNum = blockService.insertBlocks(blockchainConfig.getDataBase(), blockHashList);
            VerifyUtil.objectNotNull(rowNum, "insert block hash failed");
            VerifyUtil.numberNotZore(rowNum, "insert block hash failed");

        } catch (Exception e) {
            if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                dingdingNotification.blocksExceptionAlarm(blockchainConfig.getMainAsset(), LoggerUtil.parseErrorStacks(e));
                notificationSendTime = Instant.now().getEpochSecond();
            }
            logger.error("update block error, exception:{}", e.getLocalizedMessage());
        }
    }


    @PostConstruct
    public void init() {
        logger.info("init block height start");
        //查看blockhash表是否有记录
        Integer count = blockService.selectCountBlock(blockchainConfig.getDataBase());
        VerifyUtil.objectNotNull(count, "init height failed");
        //没有记录，则初始化一条记录
        if (count <= 0) {
            //根据配置的高度，获取高度信息
            List<Block> blockHashList = blockHandlerService.getBlocks(Arrays.asList(blockchainConfig.getInitBlockHeight()));

            //将获取的高度信息入库
            Integer rowNum = blockService.insertBlocks(blockchainConfig.getDataBase(), blockHashList);
            VerifyUtil.objectNotNull(rowNum, "insert block hash failed");
            VerifyUtil.numberNotZore(rowNum, "insert block hash failed");
        }

        logger.info("init block height complete");

        if (blockchainConfig.isHasCoinFee()) {
            logger.info("init coin fee start");
            // 初始化coin fee信息
            // 查询当前库里的区块高度
            Integer currentBlockNumber = blockHandlerService.getBlockNumber();
            // 获取当前区块信息
            List<Block> blockList = blockHandlerService.getBlocks(Arrays.asList(currentBlockNumber));
            // 获取交易池交易数量
            Integer mempoolTxCount = blockHandlerService.getMemPoolTxCount();
            // 更新coin fee表的数据
            CoinFee coinFee = new CoinFee();
            coinFee.setSymbol(blockchainConfig.getMainAsset());
            coinFee.setBlockSize(blockList.get(0).getSize());
            coinFee.setRate(BigDecimal.ZERO);
            coinFee.setBlockTxNum(blockList.get(0).getTxCount());
            coinFee.setMempoolTxNum(mempoolTxCount);
            exchangeService.insertCoinFee(coinFee);
            logger.info("init coin fee complete");
        }
    }
}
