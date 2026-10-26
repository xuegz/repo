package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntSyncErrorDao;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.SyncError;
import com.atoken.update.entity.ont.Tx;
import com.atoken.update.service.ont.OntSyncErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class OntSyncErrorServiceImpl implements OntSyncErrorService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OntSyncErrorDao ontSyncErrorDao;

    @Override
    public Integer insertSyncError(SyncError syncError) {
        return ontSyncErrorDao.insertSyncError(syncError);
    }

    @Override
    public void saveError(Block block, Tx tx, Exception e) {
        try {
            SyncError syncError = new SyncError();
            syncError.setBlockHash(block.getHash());
            syncError.setBlockHeight(block.getHeight());
            syncError.setErrorInfo(e.getLocalizedMessage());
            if (Objects.nonNull(tx)) {
                syncError.setTxHash(tx.getHash());
                syncError.setTxNum(tx.getTxNum());
            }
            this.insertSyncError(syncError);
        } catch (Exception er) {
            logger.error("save error info failed", er.getLocalizedMessage());
        }
    }
}
