package com.atoken.update.atom.jobhandler;

import com.atoken.update.core.BlockInfoCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.BlockInfoSyncJob;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
//@JobHandler(value = "atomBlockInfoSyncJobHandler")
public class AtomBlockInfoSyncJob extends BlockInfoSyncJob {
    @Autowired
    public AtomBlockInfoSyncJob(@Qualifier("atomBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
