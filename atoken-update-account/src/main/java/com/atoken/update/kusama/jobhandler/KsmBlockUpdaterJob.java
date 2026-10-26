package com.atoken.update.kusama.jobhandler;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockUpdaterJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "ksmBlockUpdaterJob")
public class KsmBlockUpdaterJob extends BlockUpdaterJob {

    @Autowired
    public KsmBlockUpdaterJob(@Qualifier("ksmBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
