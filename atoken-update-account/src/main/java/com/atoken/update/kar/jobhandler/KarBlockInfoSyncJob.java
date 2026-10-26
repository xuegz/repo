package com.atoken.update.kar.jobhandler;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockInfoSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "karBlockInfoSyncJobHandler")
public class KarBlockInfoSyncJob extends BlockInfoSyncJob {

    public KarBlockInfoSyncJob(@Qualifier("karBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("karBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
