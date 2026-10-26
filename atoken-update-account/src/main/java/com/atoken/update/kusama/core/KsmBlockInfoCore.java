package com.atoken.update.kusama.core;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.kusama.service.KsmSidecarBlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ksmBlockInfoCore")
public class KsmBlockInfoCore extends BlockInfoCore {

    @Autowired
    public KsmBlockInfoCore(@Qualifier("ksmSidecarBlockHandlerService") KsmSidecarBlockHandlerService ksmSidecarBlockHandlerService, @Autowired KsmBlockchainConfig ksmBlockchainConfig) {
        super(ksmSidecarBlockHandlerService, ksmBlockchainConfig);
    }
}
