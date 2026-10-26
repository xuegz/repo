package com.atoken.update.kar.jobhandler;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler("karBlockTxSyncJobHandler")
public class KarBlockTxSyncJob extends BlockTxSyncJob {

    @Autowired
    public KarBlockTxSyncJob(@Qualifier("karBlockTxCore") BlockTxCore blockTxCore, @Qualifier("karBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
