package com.atoken.update.atom.jobhandler;

import com.atoken.update.core.BlockTxCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.BlockTxSyncJob;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
//@JobHandler("atomBlockTxSyncJobHandler")
public class AtomBlockTxSyncJob extends BlockTxSyncJob {
    @Autowired
    public AtomBlockTxSyncJob(@Qualifier("atomBlockTxCore") BlockTxCore blockTxCore, @Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
