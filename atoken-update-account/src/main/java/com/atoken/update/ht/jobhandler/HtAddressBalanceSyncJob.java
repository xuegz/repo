package com.atoken.update.ht.jobhandler;

import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.AddressBalanceSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author legends_bai
 * @date 2020/12/9 下午4:25
 */
@Component
@JobHandler(value = "htAddressBalanceSyncJob")
public class HtAddressBalanceSyncJob extends AddressBalanceSyncJob {

    public HtAddressBalanceSyncJob(@Qualifier("htAddressBalanceCore") AddressBalanceCore addressBalanceCore, @Qualifier("htBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(addressBalanceCore, blockchainConfig);
    }
}
