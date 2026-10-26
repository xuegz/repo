package com.atoken.update.dot.jobhandler;

import com.atoken.update.core.AddressBalanceCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.AddressBalanceSyncJob;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "dotAddressBalanceSyncJobHandler")
public class DotAddressBalanceSyncJob extends AddressBalanceSyncJob {
    @Autowired
    public DotAddressBalanceSyncJob(@Qualifier("dotAddressBalanceCore") AddressBalanceCore addressBalanceCore, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(addressBalanceCore, blockchainConfig);
    }
}
