package com.atoken.update.handler;

import com.atoken.update.entity.Address;

public interface RollBackTxAfterHander {
    /**
     * 回滚交易后置处理
     * @param fromAddress
     * @param toAddress
     */
    void handler(Address fromAddress, Address toAddress);
}
