package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.Tx;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OntTxDao {
    String TABLE_COLUMN = "id, hash, from_addr fromAddr, to_addr toAddr, amount, contract_addr contractAddr, plat_type platType, block_state blockState";

    /**
     * 插入交易
     * 
     * @param tx
     * @return
     */
    @Insert("insert into " + ConstantUtil.TBALE_TX + "(hash, from_addr, to_addr, fee, amount, block_hash, block_height, state, contract_addr, gas_limit, gas_price, tx_id, type, plat_type, tx_num, status, asset_short_name, timestamp) values(#{hash}, #{fromAddr}, #{toAddr}, #{fee}, #{amount}, #{blockHash}, #{blockHeight}, #{state}, #{contractAddr}, #{gasLimit}, #{gasPrice}, #{txId}, #{type}, #{platType}, #{txNum}, #{status}, #{assetShortName}, #{timestamp})")
    @Options(useGeneratedKeys=true, keyColumn="id")
    Integer insertTx(Tx tx);

    /**
     * 更新交易信息
     * 
     * @param tx
     * @return
     */
    @Update("update tx set from_addr=#{fromAddr},fee=#{fee},block_hash=#{blockHash},block_height=#{blockHeight},state=#{state}, gas_limit=#{gasLimit}, gas_price=#{gasPrice}, asset_short_name=#{assetShortName},timestamp=#{timestamp},status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM + " where tx_id=#{txId} and status=" + ConstantUtil.TABLE_TX_STATUS_UNBLOCK)
    Integer updateTx(Tx tx);

    /**
     * 通过txid查询id
     * 
     * @param txId
     * @return
     */
    @Select("select id from tx where tx_id=#{txId}")
    Long selectIdByTxId(String txId);

    /**
     * 更新交易状态到已确认
     * 
     * @param blockHash
     * @return
     */
    @Update("update tx set status=" + ConstantUtil.TABLE_TX_STATUS_CONFIRMED + " where block_hash=#{blockHash} and status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM)
    Integer updateTxConfirm(String blockHash);

    /**
     * 更新交易状态到未上链
     * 
     * @param blockHash
     * @return
     */
    @Update("update tx set status=" + ConstantUtil.TABLE_TX_STATUS_UNBLOCK + " where block_hash=#{blockHash} and status=" + ConstantUtil.TABLE_TX_STATUS_UNCONFIRM + " and plat_type = " + ConstantUtil.TBALE_PLAT_TYPE_SEND)
    Integer updateTxUnblock(String blockHash);

    /**
     *根据区块hash删除交易
     * 
     * @param blockHash
     * @return
     */
    @Delete("delete from " + ConstantUtil.TBALE_TX + " where block_hash=#{blockHash} and plat_type = " + ConstantUtil.TBALE_PLAT_TYPE_NOT_SEND)
    Integer deleteTxByBlockHash(String blockHash);

    /**
     * 通过区块hash获取交易列表
     * 
     * @param blockHash
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from " + ConstantUtil.TBALE_TX + " where block_hash=#{blockHash}")
    List<Tx> selectTxBtBlockHash(String blockHash);

    /**
     * 查询交易是否存在
     * 
     * @param txHash
     * @param txNum
     * @return
     */
    @Select("select count(id) from " + ConstantUtil.TBALE_TX + " where hash=#{txHash} and tx_num=#{txNum}")
    Integer existTx(@Param("txHash") String txHash, @Param("txNum") Integer txNum);
}
