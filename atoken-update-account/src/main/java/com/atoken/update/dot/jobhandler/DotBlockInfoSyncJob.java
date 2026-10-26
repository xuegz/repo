package com.atoken.update.dot.jobhandler;

import com.atoken.update.core.BlockInfoCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.BlockInfoSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "dotBlockInfoSyncJobHandler")
public class DotBlockInfoSyncJob extends BlockInfoSyncJob {
    @Autowired
    public DotBlockInfoSyncJob(@Qualifier("dotBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
