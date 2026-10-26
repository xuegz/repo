package com.atoken.update.jobhandler;

import com.atoken.update.core.BlockTxCore;
import com.atoken.update.entity.BlockchainConfig;
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
        logger.info("{} block tx sync start", blockchainConfig.getMainAsset());
        blockTxCore.blockTxHandler();
        logger.info("{} block tx sync end", blockchainConfig.getMainAsset());
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " block tx sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " block tx sync success");
        return returnT;
    }
}
