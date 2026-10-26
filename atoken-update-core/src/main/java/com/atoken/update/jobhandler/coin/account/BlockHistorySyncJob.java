package com.atoken.update.jobhandler.coin.account;

import com.atoken.update.core.coin.account.BlockHistoryCore;
import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class BlockHistorySyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private BlockHistoryCore blockHistoryCore;
    private BlockchainConfig blockchainConfig;

    public BlockHistorySyncJob(BlockchainConfig blockchainConfig) {
        this.blockchainConfig = blockchainConfig;
    }


    @Override
    public ReturnT<String> execute(String s) {
        logger.info("{} block history start", blockchainConfig.getMainAsset());
        blockHistoryCore.blockHistoryHandler(blockchainConfig);
        logger.info("{} block history end", blockchainConfig.getMainAsset());
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " block history sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " block history sync success");
        return returnT;
    }
}
