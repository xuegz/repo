package com.atoken.update.xwc.jobhandler;

import com.atoken.update.core.coin.account.BlockTxRecordCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxRecordSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "xwcTxRecordSyncJob")
public class XwcTxRecordSyncJob extends BlockTxRecordSyncJob {

    public XwcTxRecordSyncJob(@Qualifier("xwcBlockTxRecordCore") BlockTxRecordCore blockTxRecordCore,@Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig){
        super(blockTxRecordCore,blockchainConfig);
    }
}
