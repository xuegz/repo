package com.atoken.update.jobhandler.coin.account;

import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

public class AddressBalanceSyncJob extends IJobHandler implements AtokenLogger{
    private AddressBalanceCore addressBalanceCore;
    private BlockchainConfig blockchainConfig;

    public AddressBalanceSyncJob(AddressBalanceCore addressBalanceCore, BlockchainConfig blockchainConfig) {
        this.addressBalanceCore = addressBalanceCore;
        this.blockchainConfig = blockchainConfig;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("{} address balance sync start", blockchainConfig.getMainAsset());
        addressBalanceCore.addressBalanceHandler();
        logger.info("{} address balance sync end",  blockchainConfig.getMainAsset());
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset()+" address balance sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " address balance sync success");
        return returnT;
    }
}
