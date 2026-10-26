package com.atoken.update.dao.ont;

import com.atoken.update.dao.ont.sql.BlockSql;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OntBlockDao {
    String BLOCK_COLUMNS = "id, hash, height, state, previous, tx_count txCount, size, timestamp, relative, synced, create_time, update_time";

    /**
     * 插入区块信息
     *
     * @param block
     * @return
     */
    @Insert("insert into block(hash, height, previous, tx_count, size, timestamp) values(#{hash}, #{height}, #{previous},#{txCount},#{size}, #{timestamp})")
    Integer insertBlock(Block block);

    /**
     * 批量插入区块信息
     *
     * @param blockList
     * @return
     */
    @InsertProvider(type = BlockSql.class, method = "insertBlocksSql")
    Integer insertBlocks(@Param("blockList") List<Block> blockList);

    /**
     * 查询最大高度
     *
     * @return
     */
    @Select("select max(height) from " + ConstantUtil.TBALE_BLOCK)
    Integer selectMaxHeight();

    /**
     * 查询未处理的区块信息列表
     *
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from " + ConstantUtil.TBALE_BLOCK + " where synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED + " order by height asc limit 1000")
    List<Block> selectBlocks();

    /**
     * 通过高度查询区块列表
     *
     * @param height
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from " + ConstantUtil.TBALE_BLOCK + " where synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + " and state=" + ConstantUtil.TABLE_BLOCK_IS_NOT_ISOLATED + " and height <= #{height}")
    List<Block> selectBlocksByHeight(Integer height);

    /**
     * 通过高度查询区块信息
     *
     * @param height
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from " + ConstantUtil.TBALE_BLOCK + " where height = #{height}")
    Block selectBlockByHeight(Integer height);

    /**
     * 更新区块同步字段
     *
     * @param id
     * @return
     */
    @Update("update " + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + " where id = #{id} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSynced(Long id);

    /**
     * 通过区块hash删除区块
     *
     * @param blockHash
     * @return
     */
    @Delete("delete from " + ConstantUtil.TBALE_BLOCK + " where hash = #{blockHash}")
    Integer deleteBlockByBlockHash(String blockHash);

    /**
     * 查询区块数量
     *
     * @return
     */
    @Select("select count(id) from " + ConstantUtil.TBALE_BLOCK)
    Integer selectCountBlock();

    /**
     * 通过hash更新同步字段
     *
     * @param blockHash
     * @param relative
     * @return
     */
    @Update("update " + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + ", relative=#{relative} where hash = #{blockHash} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSyncedByHash(@Param("blockHash") String blockHash, @Param("relative") Integer relative);
}
