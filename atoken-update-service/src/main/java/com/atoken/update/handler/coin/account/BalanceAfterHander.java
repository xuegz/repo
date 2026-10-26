package com.atoken.update.handler.coin.account;

/**
 * @author dfg
 */
public interface BalanceAfterHander {
    /**
     * 更新余额后置处理
     * @param address
     * @param walletId
     */
    void handler(String address, String walletId);
}
