package com.atoken.update.kusama.jobhandler;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ksmBlockTxSyncJobHandler")
public class KsmBlockTxSyncJob extends BlockTxSyncJob {

    @Autowired
    public KsmBlockTxSyncJob(@Qualifier("ksmBlockTxCore") BlockTxCore blockTxCore, @Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
