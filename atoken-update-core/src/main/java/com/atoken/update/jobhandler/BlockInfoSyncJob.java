package com.atoken.update.jobhandler;

import com.atoken.update.core.BlockInfoCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
        blockInfoCore.blockInfoHandler();
        logger.info("{} block sync end", blockchainConfig.getMainAsset());
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " block sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " block sync success");
        return returnT;
    }
}
