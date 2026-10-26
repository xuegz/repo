package com.atoken.update.jobhandler.coin.account;

import com.atoken.update.core.coin.account.BlockTxRecordCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

public class BlockTxRecordSyncJob extends IJobHandler implements AtokenLogger {

    private BlockTxRecordCore blockTxRecordCore;

    private BlockchainConfig blockchainConfig;

    public BlockTxRecordSyncJob(BlockTxRecordCore blockTxRecordCore,BlockchainConfig blockchainConfig){
        this.blockTxRecordCore = blockTxRecordCore;
        this.blockchainConfig = blockchainConfig;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("{} tx record job handler",this.blockchainConfig.getMainAsset());
        this.blockTxRecordCore.batchHandlerTxRecord();
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " tx record success");
        returnT.setMsg(blockchainConfig.getMainAsset()+"tx record success");
        return returnT;
    }
}
