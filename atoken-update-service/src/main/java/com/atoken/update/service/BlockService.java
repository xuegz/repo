package com.atoken.update.service;


import com.atoken.update.entity.Block;

import java.util.List;

public interface BlockService {
    Integer insertBlock(String dataBase, Block block);

    Integer insertBlocks(String dataBase, List<Block> blockList);

    Integer selectMaxHeight(String dataBase);

    List<Block> selectBlocks(String dataBase);

    Integer updateBlockSynced(String dataBase, Long id);

    Integer selectCountBlock(String dataBase);

    Integer updateBlockSyncedByHash(String dataBase, String blockHash, Integer relative);

    Block selectBlockByHeight(String dataBase, Integer height);

    List<Block> selectBlocksByHeight(String dataBase, Integer Height);

    Integer deleteBlockByBlockHash(String dataBase, String blockHash);
}
