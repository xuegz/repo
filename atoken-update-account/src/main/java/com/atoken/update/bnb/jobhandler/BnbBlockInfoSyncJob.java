package com.atoken.update.bnb.jobhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockInfoSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Component
@JobHandler(value = "bnbBlockInfoSyncJob")
public class BnbBlockInfoSyncJob extends BlockInfoSyncJob {

    @Autowired
    public BnbBlockInfoSyncJob(@Qualifier("bnbBlockInfoCore") BlockInfoCore blockInfoCore,
            @Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
