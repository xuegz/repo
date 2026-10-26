package com.atoken.update.dao.coin.account;

import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;

@Mapper
public interface AccountBlockDao extends AtokenLogger {
    String BLOCK_COLUMNS = "id, hash, height, state, previous, tx_count txCount, size, timestamp, relative, synced, create_time, update_time";

    /**
     * 插入区块信息
     * @param coin
     * @param block
     * @return
     */
    @Insert("insert into coin_account.${coin}_block(hash, height, previous, tx_count, size, timestamp) values(#{block.hash}, #{block.height}, #{block.previous},#{block.txCount},#{block.size}, #{block.timestamp})")
    Integer insertBlock(@Param("coin") String coin, @Param("block") Block block);

    /**
     * 批量插入区块信息
     * @param coin
     * @param blockList
     * @return
     */
    @InsertProvider(type = SqlProvider.class, method = "insertBlocksSql")
    Integer insertBlocks(@Param("coin") String coin, @Param("blockList") List<Block> blockList);

    /**
     * 查询最大高度
     * @param coin
     * @return
     */
    @Select("select height from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " order by height desc limit 1")
    Integer selectMaxHeight(@Param("coin") String coin);

    /**
     * 查询未处理的区块信息列表
     * @param coin
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " where synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED + " order by height asc limit 1000")
    List<Block> selectBlocks(@Param("coin") String coin);

    /**
     * 通过高度查询区块列表
     * @param coin
     * @param height
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " where synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + " and state=" + ConstantUtil.TABLE_BLOCK_IS_NOT_ISOLATED + " and height <= #{height}")
    List<Block> selectBlocksByHeight(@Param("coin") String coin, @Param("height") Integer height);

    /**
     * 通过高度查询区块信息
     * @param coin
     * @param height
     * @return
     */
    @Select("select " + BLOCK_COLUMNS + " from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " where height = #{height}")
    List<Block> selectBlockByHeight(@Param("coin") String coin, @Param("height") Integer height);

    /**
     * 更新区块同步字段
     * @param coin
     * @param id
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + " where id = #{id} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSynced(@Param("coin") String coin, @Param("id") Long id);

    /**
     * 通过区块hash删除区块
     * @param coin
     * @param blockHash
     * @return
     */
    @Delete("delete from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " where hash = #{blockHash}")
    Integer deleteBlockByBlockHash(@Param("coin") String coin, @Param("blockHash") String blockHash);

    /**
     * 查询区块数量
     * @param coin
     * @return
     */
    @Select("select count(id) from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK)
    Integer selectCountBlock(@Param("coin") String coin);

    /**
     * 通过hash更新同步字段
     * @param coin
     * @param blockHash
     * @param relative
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + ", relative=#{relative} where hash = #{blockHash} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSyncedByHash(@Param("coin") String coin, @Param("blockHash") String blockHash, @Param("relative") Integer relative);

    /**
     * 通过id更新同步字段
     * @param coin
     * @param id
     * @param relative
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " set synced = " + ConstantUtil.TABLE_BLOCK_SYNCED + ", relative=#{relative} where id = #{id} and synced = " + ConstantUtil.TABLE_BLOCK_UNSYNCED)
    Integer updateBlockSyncedById(@Param("coin") String coin, @Param("id") Long id, @Param("relative") Integer relative);

    /**
     * 通过创建时间删除
     * @param coin
     * @param createTime
     * @return
     */
    @Delete("delete from coin_account.${coin}_" + ConstantUtil.TBALE_BLOCK + " where synced = 1 and create_time <= #{createTime}")
    Integer deleteByCreateTime(@Param("coin") String coin, @Param("createTime") Date createTime);

    /**
     * 将区块信息插入历史表
     * @param coin
     * @param createTime
     * @return
     */
    @Insert("insert into coin_account.${coin}_block_history(hash, height, state, previous, tx_count, size, timestamp, relative, synced, create_time, update_time) " +
            "select hash, height, state, previous, tx_count, size, timestamp, relative, synced, create_time, update_time " +
            "from coin_account.${coin}_block where synced = 1 and create_time <= #{createTime} on duplicate key update update_time=current_timestamp")
    Integer insertSelectBlockHistory(@Param("coin") String coin, @Param("createTime") Date createTime);

    class SqlProvider {
        public String insertBlocksSql(@Param("coin") String coin, @Param("blockList") List<Block> blockList) {

            StringBuilder sql = new StringBuilder(new SQL() {
                {
                    INSERT_INTO("coin_account." + coin + "_" +ConstantUtil.TBALE_BLOCK);
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
            sql.append("ON DUPLICATE KEY UPDATE synced = 0");
            logger.debug(sql.toString());
            return sql.toString();
        }
    }
}
