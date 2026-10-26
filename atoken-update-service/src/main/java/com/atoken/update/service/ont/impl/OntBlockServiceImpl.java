package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntBlockDao;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.service.ont.OntBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OntBlockServiceImpl implements OntBlockService {
    @Autowired
    private OntBlockDao blockDao;
    @Override
    public Integer insertBlock(Block block) {
        return blockDao.insertBlock(block);
    }

    @Override
    public Integer insertBlocks(List<Block> blockList) {
        return blockDao.insertBlocks(blockList);
    }

    @Override
    public Integer selectMaxHeight() {
        return blockDao.selectMaxHeight();
    }

    @Override
    public List<Block> selectBlocks() {
        return blockDao.selectBlocks();
    }

    @Override
    public Integer updateBlockSynced(Long id) {
        return blockDao.updateBlockSynced(id);
    }

    @Override
    public Integer selectCountBlock() {
        return blockDao.selectCountBlock();
    }

    @Override
    public Integer updateBlockSyncedByHash(String blockHash, Integer relative) {
        return blockDao.updateBlockSyncedByHash(blockHash, relative);
    }

    @Override
    public Block selectBlockByHeight(Integer height) {
        return blockDao.selectBlockByHeight(height);
    }

    @Override
    public List<Block> selectBlocksByHeight(Integer height) {
        return blockDao.selectBlocksByHeight(height);
    }

    @Override
    public Integer deleteBlockByBlockHash(String blockHash) {
        return blockDao.deleteBlockByBlockHash(blockHash);
    }
}
