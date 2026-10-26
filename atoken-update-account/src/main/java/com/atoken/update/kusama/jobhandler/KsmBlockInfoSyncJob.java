package com.atoken.update.kusama.jobhandler;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.dot.jobhandler.DotBlockInfoSyncJob;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockInfoSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "ksmBlockInfoSyncJobHandler")
public class KsmBlockInfoSyncJob extends BlockInfoSyncJob {

    public KsmBlockInfoSyncJob(@Qualifier("ksmBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
