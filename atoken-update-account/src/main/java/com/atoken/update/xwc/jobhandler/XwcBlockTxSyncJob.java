package com.atoken.update.xwc.jobhandler;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.BlockTxSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author legends_bai
 * @date 2020/12/9 下午4:25
 */
@Component
@JobHandler("xwcBlockTxSyncJobHandler")
public class XwcBlockTxSyncJob extends BlockTxSyncJob {
    @Autowired
    public XwcBlockTxSyncJob(@Qualifier("xwcBlockTxCore") BlockTxCore blockTxCore, @Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockTxCore, blockchainConfig);
    }
}
