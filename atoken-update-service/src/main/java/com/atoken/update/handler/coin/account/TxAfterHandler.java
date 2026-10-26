package com.atoken.update.handler.coin.account;

import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.Tx;

/**
 * @author dfg
 */
public interface TxAfterHandler {
    /**
     * 交易后置处理
     * @param fromAddress
     * @param toAddress
     * @param tx
     */
    void handler(Address fromAddress, Address toAddress, Tx tx);
}
