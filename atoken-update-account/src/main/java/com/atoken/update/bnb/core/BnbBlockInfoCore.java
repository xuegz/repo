package com.atoken.update.bnb.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.coin.account.BlockHandlerService;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Service("bnbBlockInfoCore")
public class BnbBlockInfoCore extends BlockInfoCore {

    @Autowired
    public BnbBlockInfoCore(
            @Qualifier("bnbBlockHandlerService") BlockHandlerService blockHandlerService,
            @Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }
}
