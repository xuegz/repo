package com.atoken.update.kar.jobhandler;


import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockUpdaterJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Component
@JobHandler(value = "karBlockUpdaterSyncJob")
public class KarBlockUpdaterSyncJob extends BlockUpdaterJob {

    @Autowired
    public KarBlockUpdaterSyncJob(@Qualifier("karBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("karBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
