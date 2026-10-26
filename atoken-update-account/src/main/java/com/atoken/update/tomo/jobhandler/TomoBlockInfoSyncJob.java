package com.atoken.update.tomo.jobhandler;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockInfoSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "tomoBlockInfoSyncJobHandler")
public class TomoBlockInfoSyncJob extends BlockInfoSyncJob {
    @Autowired
    public TomoBlockInfoSyncJob(@Qualifier("tomoBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
