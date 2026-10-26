package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.Block;

import java.util.List;

public interface OntBlockService {
    Integer insertBlock(Block block);

    Integer insertBlocks(List<Block> blockList);

    Integer selectMaxHeight();

    List<Block> selectBlocks();

    Integer updateBlockSynced(Long id);

    Integer selectCountBlock();

    Integer updateBlockSyncedByHash(String blockHash, Integer relative);

    Block selectBlockByHeight(Integer height);

    List<Block> selectBlocksByHeight(Integer Height);

    Integer deleteBlockByBlockHash(String blockHash);
}
