package com.atoken.update.service.coin.account.ht;

import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.ht.HtTx;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;

public interface HtTxService {

    int saveOrUpdate(HtTx tx);

    void rollBackTx(String coin, Block parentBlock, RollBackTxAfterHander rollBackTxAfterHander);
}
