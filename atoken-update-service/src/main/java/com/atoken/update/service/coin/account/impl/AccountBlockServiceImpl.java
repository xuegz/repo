package com.atoken.update.service.coin.account.impl;

import com.atoken.update.dao.coin.account.AccountBlockDao;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.service.coin.account.AccountBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AccountBlockServiceImpl implements AccountBlockService {
    @Autowired
    private AccountBlockDao blockDao;
    @Override
    public Integer insertBlock(String coin, Block block) {
        return blockDao.insertBlock(coin, block);
    }

    @Override
    public Integer insertBlocks(String coin, List<Block> blockList) {
        return blockDao.insertBlocks(coin, blockList);
    }

    @Override
    public Integer selectMaxHeight(String coin) {
        return blockDao.selectMaxHeight(coin);
    }

    @Override
    public List<Block> selectBlocks(String coin) {
        return blockDao.selectBlocks(coin);
    }

    @Override
    public Integer updateBlockSynced(String coin, Long id) {
        return blockDao.updateBlockSynced(coin, id);
    }

    @Override
    public Integer selectCountBlock(String coin) {
        return blockDao.selectCountBlock(coin);
    }

    @Override
    public Integer updateBlockSyncedByHash(String coin, String blockHash, Integer relative) {
        return blockDao.updateBlockSyncedByHash(coin, blockHash, relative);
    }

    @Override
    public Integer updateBlockSyncedById(String coin, Long id, Integer relative) {
        return blockDao.updateBlockSyncedById(coin, id, relative);
    }

    @Override
    public List<Block> selectBlockByHeight(String coin, Integer height) {
        return blockDao.selectBlockByHeight(coin, height);
    }

    @Override
    public List<Block> selectBlocksByHeight(String coin, Integer height) {
        return blockDao.selectBlocksByHeight(coin, height);
    }

    @Override
    public Integer deleteBlockByBlockHash(String coin, String blockHash) {
        return blockDao.deleteBlockByBlockHash(coin, blockHash);
    }

    @Override
    public Integer deleteByCreateTime(String coin, Date createTime) {
        return blockDao.deleteByCreateTime(coin, createTime);
    }

    @Override
    public Integer insertSelectBlockHistory(String coin, Date createTime) {
        return blockDao.insertSelectBlockHistory(coin, createTime);
    }

    @Override
    @Transactional
    public void blockHistoryMigrate(String coin, Date createTime) {
        blockDao.insertSelectBlockHistory(coin, createTime);
        blockDao.deleteByCreateTime(coin, createTime);
    }
}
