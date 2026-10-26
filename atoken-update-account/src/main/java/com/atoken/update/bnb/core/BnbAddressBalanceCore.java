package com.atoken.update.bnb.core;

import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.handler.coin.account.BalanceAfterHander;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Service("bnbAddressBalanceCore")
public class BnbAddressBalanceCore extends AddressBalanceCore {

    @Autowired
    public BnbAddressBalanceCore(
            @Qualifier("bnbBlockHandlerService") BlockHandlerService blockHandlerService,
            @Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public BalanceAfterHander balanceAfterHander() {
        return null;
    }
}
