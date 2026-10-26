package com.atoken.update.dao;

import com.atoken.update.entity.SyncError;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SyncErrorDao {
    /**
     * 插入错误信息
     *
     * @param dataBase
     * @param syncError
     * @return
     */
    @Insert("insert into ${dataBase}." + ConstantUtil.TABLE_SYNC_ERROR + "(block_height, block_hash, tx_hash, tx_num, error_info) values(#{syncError.blockHeight}, #{syncError.blockHash}, #{syncError.txHash}, #{syncError.txNum}, #{syncError.errorInfo})")
    Integer insertSyncError(@Param("dataBase") String dataBase, @Param("syncError") SyncError syncError);
}
