package com.atoken.update.dao.coin.account;

import com.atoken.update.entity.coin.account.SyncError;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountSyncErrorDao {
    /**
     * 插入错误信息
     * @param coin
     * @param syncError
     * @return
     */
    @Insert("insert into coin_account.${coin}_" + ConstantUtil.TABLE_SYNC_ERROR + "(block_height, block_hash, tx_hash, tx_num, error_info) values(#{syncError.blockHeight}, #{syncError.blockHash}, #{syncError.txHash}, #{syncError.txNum}, #{syncError.errorInfo})")
    Integer insertSyncError(@Param("coin") String coin, @Param("syncError") SyncError syncError);
}
