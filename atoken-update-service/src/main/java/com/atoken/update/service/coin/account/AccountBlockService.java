package com.atoken.update.service.coin.account;


import java.util.Date;
import java.util.List;
import com.atoken.update.entity.coin.account.Block;

public interface AccountBlockService {
    Integer insertBlock(String coin, Block block);

    Integer insertBlocks(String coin, List<Block> blockList);

    Integer selectMaxHeight(String coin);

    List<Block> selectBlocks(String coin);

    Integer updateBlockSynced(String coin, Long id);

    Integer selectCountBlock(String coin);

    Integer updateBlockSyncedByHash(String coin, String blockHash, Integer relative);

    Integer updateBlockSyncedById(String coin, Long id, Integer relative);

    List<Block> selectBlockByHeight(String coin, Integer height);

    List<Block> selectBlocksByHeight(String coin, Integer Height);

    Integer deleteBlockByBlockHash(String coin, String blockHash);

    Integer deleteByCreateTime(String coin, Date createTime);

    Integer insertSelectBlockHistory(String coin, Date createTime);

    void blockHistoryMigrate(String coin, Date createTime);
}
