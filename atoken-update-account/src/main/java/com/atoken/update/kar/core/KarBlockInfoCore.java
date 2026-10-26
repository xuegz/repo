package com.atoken.update.kar.core;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.kar.config.KarBlockchainConfig;
import com.atoken.update.kar.service.KarSidecarBlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("karBlockInfoCore")
public class KarBlockInfoCore extends BlockInfoCore {

    @Autowired
    public KarBlockInfoCore(@Qualifier("karSidecarBlockHandlerService") KarSidecarBlockHandlerService karSidecarBlockHandlerService, @Autowired KarBlockchainConfig karBlockchainConfig) {
        super(karSidecarBlockHandlerService, karBlockchainConfig);
    }
}
