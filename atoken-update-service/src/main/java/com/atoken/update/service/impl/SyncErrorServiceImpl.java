package com.atoken.update.service.impl;

import com.atoken.update.dao.SyncErrorDao;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.SyncError;
import com.atoken.update.entity.Tx;
import com.atoken.update.service.SyncErrorService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
public class SyncErrorServiceImpl implements SyncErrorService, AtokenLogger {
    @Autowired
    private SyncErrorDao syncErrorDao;

    @Override
    public Integer insertSyncError(String dataBase, SyncError syncError) {
        return syncErrorDao.insertSyncError(dataBase, syncError);
    }

    @Override
    @Transactional
    public void saveError(String dataBase, Block block, Tx tx, Exception e) {
        try {
            SyncError syncError = new SyncError();
            syncError.setBlockHash(block.getHash());
            syncError.setBlockHeight(block.getHeight());
            syncError.setErrorInfo(e.getLocalizedMessage());
            if (Objects.nonNull(tx)) {
                syncError.setTxHash(tx.getHash());
                syncError.setTxNum(tx.getTxNum());
            }
            this.insertSyncError(dataBase, syncError);
        } catch (Exception er) {
            logger.error("save error info failed", er.getLocalizedMessage());
        }
    }
}
