package com.atoken.update.xwc.core;

import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.handler.coin.account.BalanceAfterHander;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author legends_bai
 * @date 2020/12/9 下午4:48
 */
@Service("xwcAddressBalanceCore")
public class XwcAddressBalanceCore extends AddressBalanceCore {

    @Autowired
    public XwcAddressBalanceCore(@Qualifier("xwcBlockHandlerService")BlockHandlerService blockHandlerService, @Qualifier("xwcBlockchainConfig")BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public BalanceAfterHander balanceAfterHander() {
        return null;
    }
}
