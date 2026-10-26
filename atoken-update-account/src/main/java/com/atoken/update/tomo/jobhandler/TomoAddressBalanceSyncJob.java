package com.atoken.update.tomo.jobhandler;

import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.AddressBalanceSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "tomoAddressBalanceSyncJobHandler")
public class TomoAddressBalanceSyncJob extends AddressBalanceSyncJob {
    @Autowired
    public TomoAddressBalanceSyncJob(@Qualifier("tomoAddressBalanceCore") AddressBalanceCore addressBalanceCore, @Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(addressBalanceCore, blockchainConfig);
    }
}
