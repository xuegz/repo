package com.atoken.update.service.coin.account;


import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.SyncError;
import com.atoken.update.entity.coin.account.Tx;

public interface AccountSyncErrorService {
    Integer insertSyncError(String coin, SyncError syncError);

    void saveError(String coin, Block block, Tx tx, Exception e);
}
