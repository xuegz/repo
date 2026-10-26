package com.atoken.update.atom.jobhandler;

import com.atoken.update.atom.core.AtomAddressBalanceCore;
import com.atoken.update.core.AddressBalanceCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.jobhandler.AddressBalanceSyncJob;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
//@JobHandler(value = "atomAddressBalanceSyncJobHandler")
public class AtomAddressBalanceSyncJob extends AddressBalanceSyncJob {
    @Autowired
    public AtomAddressBalanceSyncJob(@Qualifier("atomAddressBalanceCore") AddressBalanceCore addressBalanceCore, @Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(addressBalanceCore, blockchainConfig);
    }
}
