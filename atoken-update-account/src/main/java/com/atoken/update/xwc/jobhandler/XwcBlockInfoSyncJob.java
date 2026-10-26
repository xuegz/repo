package com.atoken.update.xwc.jobhandler;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockInfoSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author legends_bai
 * @date 2020/12/9 下午4:25
 */
@Component
@JobHandler(value = "xwcBlockInfoSyncJob")
public class XwcBlockInfoSyncJob extends BlockInfoSyncJob {

    @Autowired
    public XwcBlockInfoSyncJob(@Qualifier("xwcBlockInfoCore") BlockInfoCore blockInfoCore, @Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockInfoCore, blockchainConfig);
    }
}
