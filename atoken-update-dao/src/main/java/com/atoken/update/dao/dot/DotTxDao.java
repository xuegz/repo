package com.atoken.update.dao.dot;

import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotTx;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DotTxDao {

    String TABLE_COLUMN = "id, hash, from_addr fromAddr, to_addr toAddr, fee, amount, plat_type platType, block_state blockState";

    /**
     * 通过txid查询id
     * @param dataBase
     * @param txId
     * @return
     */
    @Select("select id from ${dataBase}.tx where tx_id=#{txId}")
    Long selectIdByTxId(@Param("dataBase") String dataBase, @Param("txId") String txId);

    /**
     * 更新交易状态到已确认
     * @param dataBase
     * @param blockHash
     * @return
     */
    @Update("update ${dataBase}.tx set status=" + ConstantUtil.TABLE_TX_STATUS_CONFIRMED + " where block_hash=#{blockHash} and status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM)
    Integer updateTxConfirm(@Param("dataBase") String dataBase, @Param("blockHash") String blockHash);

    /**
     * 更新交易状态到未上链
     * @param dataBase
     * @param blockHash
     * @return
     */
    @Update("update ${dataBase}.tx set status=" + ConstantUtil.TABLE_TX_STATUS_UNBLOCK + " where block_hash=#{blockHash} and status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM + " and plat_type = " + ConstantUtil.TBALE_PLAT_TYPE_SEND)
    Integer updateTxUnblock(@Param("dataBase") String dataBase, @Param("blockHash") String blockHash);

    /**
     *根据区块hash删除交易
     * @param dataBase
     * @param blockHash
     * @return
     */
    @Delete("delete from ${dataBase}." + ConstantUtil.TBALE_TX + " where block_hash=#{blockHash} and plat_type = " + ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND)
    Integer deleteTxByBlockHash(@Param("dataBase") String dataBase, @Param("blockHash") String blockHash);

    /**
     * 通过区块hash获取交易列表
     * @param dataBase
     * @param blockHash
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from ${dataBase}." + ConstantUtil.TBALE_TX + " where block_hash=#{blockHash}")
    List<Tx> selectTxByBlockHash(@Param("dataBase") String dataBase, @Param("blockHash") String blockHash);

    /**
     * 查询交易是否存在
     * @param dataBase
     * @param txHash
     * @param txNum
     * @return
     */
    @Select("select count(id) from ${dataBase}." + ConstantUtil.TBALE_TX + " where hash=#{txHash} and tx_num=#{txNum}")
    Integer existTx(@Param("dataBase") String dataBase, @Param("txHash") String txHash, @Param("txNum") Integer txNum);

    /**
     * 插入交易
     *
     * @param dataBase
     * @param tx
     * @return
     */
    @Insert("insert into ${dataBase}." + ConstantUtil.TBALE_TX +
            "(hash, from_addr, to_addr, fee, amount, block_hash, block_height, state, gas_limit, gas_price, tx_id, " +
            "type, plat_type, tx_num, status, asset_short_name, timestamp, comment, nonce, main_coin, tip) " +
            "values(#{tx.hash}, #{tx.fromAddr}, #{tx.toAddr}, #{tx.fee}, #{tx.amount}, #{tx.blockHash}, " +
            "#{tx.blockHeight}, #{tx.state}, #{tx.gasLimit}, #{tx.gasPrice}, #{tx.txId}, #{tx.type}, #{tx.platType}," +
            " #{tx.txNum}, #{tx.status}, #{tx.assetShortName}, #{tx.timestamp}, #{tx.comment}, " +
            "#{tx.nonce}, #{tx.mainCoin}, #{tx.tip})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "tx.id")
    Integer insertTx(@Param("dataBase") String dataBase, @Param("tx") DotTx tx);

    /**
     * 更新交易信息
     *
     * @param dataBase
     * @param tx
     * @return
     */
    @Update("update ${dataBase}.tx set from_addr=#{tx.fromAddr}, amount=#{tx.amount}, fee=#{tx.fee}, " +
            "block_hash=#{tx.blockHash}, block_height=#{tx.blockHeight}, state=#{tx.state}, " +
            "gas_limit=#{tx.gasLimit}, gas_price=#{tx.gasPrice}, asset_short_name=#{tx.assetShortName}," +
            "timestamp=#{tx.timestamp},type=#{tx.type},main_coin=#{tx.mainCoin},comment=#{tx.comment},tip=#{tx.tip}," +
            "status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM +
            " where tx_id=#{tx.txId} and status=" + ConstantUtil.TABLE_TX_STATUS_UNBLOCK)
    Integer updateTx(@Param("dataBase") String dataBase, @Param("tx") DotTx tx);

}
