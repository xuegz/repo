package com.atoken.update.bnb.jobhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Component
@JobHandler("bnbBlockTxSyncJo")
public class BnbBlockTxSyncJob extends BlockTxSyncJob {
    @Autowired
    public BnbBlockTxSyncJob(@Qualifier("bnbBlockTxCore") BlockTxCore blockTxCore,
            @Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
