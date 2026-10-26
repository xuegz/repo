package com.atoken.update.service.impl;

import com.atoken.update.dao.BlockDao;
import com.atoken.update.entity.Block;
import com.atoken.update.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockServiceImpl implements BlockService {
    @Autowired
    private BlockDao blockDao;
    @Override
    public Integer insertBlock(String dataBase, Block block) {
        return blockDao.insertBlock(dataBase, block);
    }

    @Override
    public Integer insertBlocks(String dataBase, List<Block> blockList) {
        return blockDao.insertBlocks(dataBase, blockList);
    }

    @Override
    public Integer selectMaxHeight(String dataBase) {
        return blockDao.selectMaxHeight(dataBase);
    }

    @Override
    public List<Block> selectBlocks(String dataBase) {
        return blockDao.selectBlocks(dataBase);
    }

    @Override
    public Integer updateBlockSynced(String dataBase, Long id) {
        return blockDao.updateBlockSynced(dataBase, id);
    }

    @Override
    public Integer selectCountBlock(String dataBase) {
        return blockDao.selectCountBlock(dataBase);
    }

    @Override
    public Integer updateBlockSyncedByHash(String dataBase, String blockHash, Integer relative) {
        return blockDao.updateBlockSyncedByHash(dataBase, blockHash, relative);
    }

    @Override
    public Block selectBlockByHeight(String dataBase, Integer height) {
        return blockDao.selectBlockByHeight(dataBase, height);
    }

    @Override
    public List<Block> selectBlocksByHeight(String dataBase, Integer height) {
        return blockDao.selectBlocksByHeight(dataBase, height);
    }

    @Override
    public Integer deleteBlockByBlockHash(String dataBase, String blockHash) {
        return blockDao.deleteBlockByBlockHash(dataBase, blockHash);
    }
}
