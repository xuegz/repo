package com.atoken.update.dot.jobhandler;

import com.atoken.update.core.BlockTxRecordCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.BlockTxRecordSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler("dotTxRecordSyncJob")
public class DotTxRecordSyncJob extends BlockTxRecordSyncJob {

    public DotTxRecordSyncJob(@Qualifier("dotTxRecordCore") BlockTxRecordCore blockTxRecordCore, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxRecordCore, blockchainConfig);
    }
}
