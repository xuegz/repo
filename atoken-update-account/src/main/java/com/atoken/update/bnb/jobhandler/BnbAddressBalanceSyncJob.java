package com.atoken.update.bnb.jobhandler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.AddressBalanceSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;

/**
 * @author legends_bai
 * @date 2020/12/9 下午4:25
 */
@Component
@JobHandler(value = "bnbAddressBalanceSyncJob")
public class BnbAddressBalanceSyncJob extends AddressBalanceSyncJob {

    public BnbAddressBalanceSyncJob(
            @Qualifier("bnbAddressBalanceCore") AddressBalanceCore addressBalanceCore,
            @Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(addressBalanceCore, blockchainConfig);
    }
}
