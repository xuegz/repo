package com.atoken.update.ht.core;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Service("htBlockInfoCore")
public class HtBlockInfoCore extends BlockInfoCore {

    @Autowired
    public HtBlockInfoCore(@Qualifier("htBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("htBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }
}
