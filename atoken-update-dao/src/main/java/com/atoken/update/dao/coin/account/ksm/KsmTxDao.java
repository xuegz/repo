package com.atoken.update.dao.coin.account.ksm;

import com.atoken.update.dao.TxDao;
import com.atoken.update.entity.Tx;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;
import com.atoken.update.entity.coin.account.ksm.KsmTx;

import java.util.List;

@Mapper
public interface KsmTxDao {

    final String TABLE_COLUMN = "id, hash, from_addr fromAddr, to_addr toAddr, fee, amount, plat_type platType, block_state blockState";

    /**
     * 根据hash查询交易
     * @param coin
     * @param hash
     * @return
     */
    @Select("select id, hash, state, status from coin_account.${coin}_"+ConstantUtil.TBALE_TX+" where hash = #{hash}")
    Tx selectTxByHash(@Param("coin") String coin,@Param("hash") String hash);

    /**
     * 通过txid查询id
     * @param coin
     * @param txId
     * @return
     */
    @Select("select id from coin_account.${coin}_"+ConstantUtil.TBALE_TX+" where tx_id=#{txId}")
    Long selectIdByTxId(@Param("coin") String coin,@Param("txId") String txId);

    /**
     * 更新交易状态到已确认
     * @param coin
     * @param blockHash
     * @return
     */
    @Update("update coin_account.${coin}_"+ConstantUtil.TBALE_TX+" set status=" + ConstantUtil.TABLE_TX_STATUS_CONFIRMED + " where block_hash=#{blockHash} and status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM)
    Integer updateTxConfirm(@Param("coin") String coin,@Param("blockHash") String blockHash);

    /**
     * 更新交易状态到未上链
     * @param coin
     * @param blockHash
     * @return
     */
    @Update("update coin_account.${coin}_"+ConstantUtil.TBALE_TX+" set status=" + ConstantUtil.TABLE_TX_STATUS_UNBLOCK + " where block_hash=#{blockHash} and status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM + " and plat_type = " + ConstantUtil.TBALE_PLAT_TYPE_SEND)
    Integer updateTxUnblock(@Param("coin") String coin,@Param("blockHash") String blockHash);

    /**
     *根据区块hash删除交易
     * @param coin
     * @param blockHash
     * @return
     */
    @Delete("delete from coin_account.${coin}_" + ConstantUtil.TBALE_TX + " where block_hash=#{blockHash} and plat_type = " + ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND)
    Integer deleteTxByBlockHash(@Param("coin") String coin,@Param("blockHash") String blockHash);

    /**
     * 通过区块hash获取交易列表
     * @param coin
     * @param blockHash
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from coin_account.${coin}_" + ConstantUtil.TBALE_TX + " where block_hash=#{blockHash}")
    List<Tx> selectTxByBlockHash(@Param("coin") String coin,@Param("blockHash") String blockHash);

    /**
     * 查询交易是否存在
     * @param coin
     * @param txHash
     * @param txNum
     * @return
     */
    @Select("select count(id) from coin_account.${coin}_" + ConstantUtil.TBALE_TX + " where hash=#{txHash} and tx_num=#{txNum}")
    Integer existTx(@Param("coin") String coin, @Param("txHash") String txHash, @Param("txNum") Integer txNum);

    /**
     * 插入交易
     *
     * @param coin
     * @param tx
     * @return
     */
    @Insert("insert into coin_account.${coin}_" + ConstantUtil.TBALE_TX +
            "(hash, from_addr, to_addr, fee, amount, block_hash, block_height, state, gas_limit, gas_price, tx_id, " +
            "type, plat_type, tx_num, status, asset_short_name, timestamp, comment, nonce, main_coin, tip) " +
            "values(#{tx.hash}, #{tx.fromAddr}, #{tx.toAddr}, #{tx.fee}, #{tx.amount}, #{tx.blockHash}, " +
            "#{tx.blockHeight}, #{tx.state}, #{tx.gasLimit}, #{tx.gasPrice}, #{tx.txId}, #{tx.type}, #{tx.platType}," +
            " #{tx.txNum}, #{tx.status}, #{tx.assetShortName}, #{tx.timestamp}, #{tx.comment}, " +
            "#{tx.nonce}, #{tx.mainCoin}, #{tx.tip})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "tx.id")
    Integer insertTx(@Param("coin") String coin, @Param("tx") KsmTx tx);

    /**
     * 更新交易信息
     *
     * @param coin
     * @param tx
     * @return
     */
    @Update("update coin_account.${coin}_"+ConstantUtil.TBALE_TX+" set from_addr=#{tx.fromAddr}, amount=#{tx.amount}, fee=#{tx.fee}, " +
            "block_hash=#{tx.blockHash}, block_height=#{tx.blockHeight}, state=#{tx.state}, " +
            "gas_limit=#{tx.gasLimit}, gas_price=#{tx.gasPrice}, asset_short_name=#{tx.assetShortName}," +
            "timestamp=#{tx.timestamp},type=#{tx.type},main_coin=#{tx.mainCoin},comment=#{tx.comment},tip=#{tx.tip}," +
            "status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM +
            " where tx_id=#{tx.txId} and status=" + ConstantUtil.TABLE_TX_STATUS_UNBLOCK)
    Integer updateTx(@Param("coin") String coin, @Param("tx") KsmTx tx);

    /**
     * 更新交易状态
     * @param id
     * @return
     */
    @Update("update coin_account.${coin}_"+ConstantUtil.TBALE_TX+" set status = 2 where id = #{id}")
    Integer updateTxStatus(@Param("coin")String coin,@Param("id") Long id);

    /**
     * 查询解绑的交易
     * @param unbondHeight
     * @return
     */
    @Select("select "+ TxDao.TABLE_COLUMN +" from coin_account.${coin}_"+ConstantUtil.TBALE_TX+" where block_height < #{unbondHeight} and type = 'UNBOND' and status = 1")
    List<Tx> selectUnbondTxes(@Param("coin")String coin,@Param("unbondHeight") Integer unbondHeight);
}
