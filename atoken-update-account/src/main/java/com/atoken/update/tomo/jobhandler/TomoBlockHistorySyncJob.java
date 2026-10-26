package com.atoken.update.tomo.jobhandler;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockHistorySyncJob;
import com.atoken.update.jobhandler.coin.account.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler("tomoBlockHistorySyncJobHandler")
public class TomoBlockHistorySyncJob extends BlockHistorySyncJob {
    @Autowired
    public TomoBlockHistorySyncJob( @Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }
}
