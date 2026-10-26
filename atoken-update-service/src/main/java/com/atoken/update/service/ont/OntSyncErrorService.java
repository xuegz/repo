package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.SyncError;
import com.atoken.update.entity.ont.Tx;

public interface OntSyncErrorService {
    Integer insertSyncError(SyncError syncError);

    void saveError(Block block, Tx tx, Exception e);
}
