package com.atoken.update.handler.coin.account;

import com.atoken.update.entity.coin.account.Address;

/**
 * @author dfg
 */
public interface RollBackTxAfterHander {
    /**
     * 回滚交易后置处理
     * @param fromAddress
     * @param toAddress
     */
    void handler(Address fromAddress, Address toAddress);
}
