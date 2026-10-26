package com.atoken.update.xwc.core;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author legends_bai
 * @date 2020/12/9 下午5:18
 */
@Service("xwcBlockInfoCore")
public class XwcBlockInfoCore extends BlockInfoCore {

    @Autowired
    public XwcBlockInfoCore(@Qualifier("xwcBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }
}
