package com.atoken.update.handler;

import com.atoken.update.entity.Address;
import com.atoken.update.entity.Tx;

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
