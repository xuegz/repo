package com.atoken.update.dao;

import com.atoken.update.entity.Block;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

@Mapper
public interface BlockDao extends AtokenLogger {
    String BLOCK_COLUMNS = "id, hash, height, state, previous, tx_count txCount, size, timestamp, relative, synced, create_time, update_time";

    /**
     * 插入区块信息
     * @param dataBase
     * @param block
     * @return
     */
    @Insert("insert into ${dataBase}.block(hash, height, previous, tx_count, size, timestamp) values(#{block.hash}, #{block.height}, #{block.previous},#{block.txCount},#{block.size}, #{block.timestamp})")
    Integer insertBlock(@Param("dataBase") String dataBase, @Param("block") Block block);

    /**
     * 批量插入区块信息
     * @param dataBase
     * @param blockList
     * @return
     */
    @InsertProvider(type = SqlProvider.class, method = "insertBlocksSql")
    Integer insertBlocks(@Param("dataBase") String dataBase, @Param("blockList") List<Block> blockList);

    /**
     * 查询最大高度
     * @param dataBase
     * @return
     */
    @Select("select max(height) from ${dataBase}." + ConstantUtil.TBALE_BLOCK)
    Integer selectMaxHeight(@Param("dataBase") String dataBase);

    /**
     * 查询未处理的区块信息列表
     * @param dataBase
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from ${dataBase}." + ConstantUtil.TBALE_BLOCK + " where synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED + " order by height asc limit 1000")
    List<Block> selectBlocks(@Param("dataBase") String dataBase);

    /**
     * 通过高度查询区块列表
     * @param dataBase
     * @param height
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from ${dataBase}." + ConstantUtil.TBALE_BLOCK + " where synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + " and state=" + ConstantUtil.TABLE_BLOCK_IS_NOT_ISOLATED + " and height <= #{height}")
    List<Block> selectBlocksByHeight(@Param("dataBase") String dataBase, @Param("height") Integer height);

    /**
     * 通过高度查询区块信息
     * @param dataBase
     * @param height
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from ${dataBase}." + ConstantUtil.TBALE_BLOCK + " where height = #{height}")
    Block selectBlockByHeight(@Param("dataBase") String dataBase, @Param("height") Integer height);

    /**
     * 更新区块同步字段
     * @param dataBase
     * @param id
     * @return
     */
    @Update("update ${dataBase}." + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + " where id = #{id} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSynced(@Param("dataBase") String dataBase, @Param("id") Long id);

    /**
     * 通过区块hash删除区块
     * @param dataBase
     * @param blockHash
     * @return
     */
    @Delete("delete from ${dataBase}." + ConstantUtil.TBALE_BLOCK + " where hash = #{blockHash}")
    Integer deleteBlockByBlockHash(@Param("dataBase") String dataBase, @Param("blockHash") String blockHash);

    /**
     * 查询区块数量
     * @param dataBase
     * @return
     */
    @Select("select count(id) from ${dataBase}." + ConstantUtil.TBALE_BLOCK)
    Integer selectCountBlock(@Param("dataBase") String dataBase);

    /**
     * 通过hash更新同步字段
     * @param dataBase
     * @param blockHash
     * @param relative
     * @return
     */
    @Update("update ${dataBase}." + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + ", relative=#{relative} where hash = #{blockHash} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSyncedByHash(@Param("dataBase") String dataBase, @Param("blockHash") String blockHash, @Param("relative") Integer relative);

    class SqlProvider {
        public String insertBlocksSql(@Param("dataBase") String dataBase, @Param("blockList") List<Block> blockList) {

            StringBuilder sql = new StringBuilder(new SQL() {
                {
                    INSERT_INTO(dataBase + "." +ConstantUtil.TBALE_BLOCK);
                    INTO_COLUMNS("hash", "height", "previous", "tx_count", "size", "timestamp");
                }
            }.toString());
            sql.append("values")
                    .append(blockList.stream()
                            .map(block -> "('" + block.getHash() + "', "
                                    + block.getHeight() + ",'" + block.getPrevious()
                                    + "'," + block.getTxCount() + "," + block.getSize()
                                    + "," + block.getTimestamp() + ")")
                            .reduce((temp, item) -> temp + "," + item).get());
            logger.debug(sql.toString());
            return sql.toString();
        }
    }
}
