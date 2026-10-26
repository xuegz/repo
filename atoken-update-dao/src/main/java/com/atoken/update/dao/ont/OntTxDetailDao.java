package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.TxDetail;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OntTxDetailDao {
    /**
     *
     * @param txDetail
     * @return
     */
    @Insert("insert into "+ ConstantUtil.TABLE_TX_DETAIL +"(tx_hash, wallet_id, addr, amount, block_height, block_hash, timestamp, state, contract_addr, fee, tx_num, tx_id, is_entry) value(#{txHash},#{walletId},#{addr},#{amount},#{blockHeight},#{blockHash},#{timestamp},#{state},#{contractAddr},#{fee},#{txNum},#{txId},#{isEntry})")
    Integer insertTxDetail(TxDetail txDetail);

    @Delete("delete from " + ConstantUtil.TABLE_TX_DETAIL + " where block_hash=#{blockHash}")
    Integer deleteTxDetailByBlockHash(String blockHash);

    @Update("update " + ConstantUtil.TABLE_TX_DETAIL + " set del = " + ConstantUtil.TABLE_DELETED + " where block_hash = #{blockHash}")
    Integer deleteTxDetail(String blockHash);
}
