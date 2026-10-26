package com.atoken.update.dot.jobhandler;

import com.atoken.update.core.BlockTxCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler("dotBlockTxSyncJobHandler")
public class DotBlockTxSyncJob extends BlockTxSyncJob {
    @Autowired
    public DotBlockTxSyncJob(@Qualifier("dotBlockTxCore") BlockTxCore blockTxCore, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
