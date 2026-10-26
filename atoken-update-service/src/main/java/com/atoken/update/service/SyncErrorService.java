package com.atoken.update.service;

import com.atoken.update.entity.Block;
import com.atoken.update.entity.SyncError;
import com.atoken.update.entity.Tx;

public interface SyncErrorService {
    Integer insertSyncError(String dataBase, SyncError syncError);

    void saveError(String dataBase, Block block, Tx tx, Exception e);
}
