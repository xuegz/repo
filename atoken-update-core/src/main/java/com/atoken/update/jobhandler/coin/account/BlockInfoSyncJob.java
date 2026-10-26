package com.atoken.update.jobhandler.coin.account;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

public class BlockInfoSyncJob extends IJobHandler implements AtokenLogger {
    private BlockInfoCore blockInfoCore;
    private BlockchainConfig blockchainConfig;

    public BlockInfoSyncJob(BlockInfoCore blockInfoCore, BlockchainConfig blockchainConfig) {
        this.blockInfoCore = blockInfoCore;
        this.blockchainConfig = blockchainConfig;
    }


    @Override
    public ReturnT<String> execute(String s) {
        logger.info("{} block sync start", blockchainConfig.getMainAsset());
        long startTime = System.currentTimeMillis();
        blockInfoCore.blockInfoHandler();
        long endTime = System.currentTimeMillis();
        logger.info("{} block sync end,time:{}", blockchainConfig.getMainAsset(),endTime-startTime);
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " block sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " block sync success");
        return returnT;
    }
}
