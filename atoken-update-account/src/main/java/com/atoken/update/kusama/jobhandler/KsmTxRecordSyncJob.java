package com.atoken.update.kusama.jobhandler;

import com.atoken.update.core.coin.account.BlockTxRecordCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxRecordSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ksmTxRecordSyncJob")
public class KsmTxRecordSyncJob extends BlockTxRecordSyncJob {

    public KsmTxRecordSyncJob(@Qualifier("ksmBlockTxRecordCore") BlockTxRecordCore blockTxRecordCore,@Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxRecordCore, blockchainConfig);
    }
}
