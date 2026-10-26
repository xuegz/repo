package com.atoken.update.service.coin.account.ht;

import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.ht.HtTokenTx;

public interface HtTokenTxService {

    int saveOrUpdate(HtTokenTx tokenTx);

    int removeUnconfirmByTxHash(String txHash);

    void rollBackTx(Block parentBlock);
}
