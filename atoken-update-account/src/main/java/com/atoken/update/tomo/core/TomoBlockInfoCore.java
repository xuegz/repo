package com.atoken.update.tomo.core;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("tomoBlockInfoCore")
public class TomoBlockInfoCore extends BlockInfoCore {
    @Autowired
    public TomoBlockInfoCore(@Qualifier("tomoBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }
}
