package com.atoken.update.jobhandler.coin.account;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

public class BlockTxSyncJob extends IJobHandler implements AtokenLogger {
    private BlockTxCore blockTxCore;
    private BlockchainConfig blockchainConfig;

    public BlockTxSyncJob(BlockTxCore blockTxCore, BlockchainConfig blockchainConfig) {
        this.blockTxCore = blockTxCore;
        this.blockchainConfig = blockchainConfig;
    }

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("===={} block tx sync start", blockchainConfig.getMainAsset());
        long startTime = System.currentTimeMillis();
        blockTxCore.blockTxHandler();
        long endTime = System.currentTimeMillis();
        logger.info("===={} block tx sync end,time{}", blockchainConfig.getMainAsset(),endTime-startTime);
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " block tx sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " block tx sync success");
        return returnT;
    }
}
