package com.atoken.update.service.coin.account.bnb;

import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.bnb.BnbTx;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;

public interface BnbTxService {

    int saveOrUpdate(BnbTx tx);

    void rollBackTx(String coin, Block parentBlock, RollBackTxAfterHander rollBackTxAfterHander);
}
