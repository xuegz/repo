package com.atoken.update.xwc.core;

import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.TransferMessage;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author legends_bai
 * @date 2020/12/9 下午5:21
 */
@Service("xwcBlockTxCore")
public class XwcBlockTxCore extends BlockTxCore {

    @Autowired
    public XwcBlockTxCore(@Qualifier("xwcBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public TransferMessage getTransferMessage() {
        return null;
    }

    @Override
    public TxAfterHandler txAfterHandler() {
        return null;
    }

    @Override
    public RollBackTxAfterHander rollBackTxAfterHander() {
        return null;
    }


}
