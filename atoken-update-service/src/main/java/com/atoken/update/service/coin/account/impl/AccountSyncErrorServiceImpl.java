package com.atoken.update.service.coin.account.impl;

import com.atoken.update.dao.coin.account.AccountSyncErrorDao;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.SyncError;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.service.coin.account.AccountSyncErrorService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
public class AccountSyncErrorServiceImpl implements AccountSyncErrorService, AtokenLogger {
    @Autowired
    private AccountSyncErrorDao syncErrorDao;

    @Override
    public Integer insertSyncError(String coin, SyncError syncError) {
        return syncErrorDao.insertSyncError(coin, syncError);
    }

    @Override
    @Transactional
    public void saveError(String coin, Block block, Tx tx, Exception e) {
        try {
            SyncError syncError = new SyncError();
            syncError.setBlockHash(block.getHash());
            syncError.setBlockHeight(block.getHeight());
            syncError.setErrorInfo(e.getLocalizedMessage());
            if (Objects.nonNull(tx)) {
                syncError.setTxHash(tx.getHash());
                syncError.setTxNum(tx.getTxNum());
            }
            this.insertSyncError(coin, syncError);
        } catch (Exception er) {
            logger.error("save error info failed", er.getLocalizedMessage());
        }
    }
}
