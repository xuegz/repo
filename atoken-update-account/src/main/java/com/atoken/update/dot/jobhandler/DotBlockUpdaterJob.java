package com.atoken.update.dot.jobhandler;

import com.atoken.update.core.BlockInfoCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.BlockUpdaterJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "dotBlockUpdaterJob")
public class DotBlockUpdaterJob extends BlockUpdaterJob {
    @Autowired
    public DotBlockUpdaterJob(@Qualifier("dotBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
