package com.atoken.update.ht.jobhandler;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Component
@JobHandler("htBlockTxSyncJo")
public class HtBlockTxSyncJob extends BlockTxSyncJob {
    @Autowired
    public HtBlockTxSyncJob(@Qualifier("htBlockTxCore") BlockTxCore blockTxCore, @Qualifier("htBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
