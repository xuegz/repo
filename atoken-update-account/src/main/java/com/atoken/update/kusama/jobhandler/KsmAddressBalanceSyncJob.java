package com.atoken.update.kusama.jobhandler;

import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.jobhandler.coin.account.AddressBalanceSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "ksmAddressBalanceSyncJobHandler")
public class KsmAddressBalanceSyncJob extends AddressBalanceSyncJob {

    @Autowired
    public KsmAddressBalanceSyncJob(@Qualifier("ksmAddressBalanceCore") AddressBalanceCore addressBalanceCore, @Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(addressBalanceCore, blockchainConfig);
    }
}
