package com.atoken.update.service.coin.account.bnb;

import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.bnb.BnbTokenTx;

public interface BnbTokenTxService {

    int saveOrUpdate(BnbTokenTx tokenTx);

    int removeUnconfirmByTxHash(String txHash);

    void rollBackTx(Block parentBlock);
}
