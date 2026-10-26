package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.SyncError;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OntSyncErrorDao {
    /**
     * 插入错误信息
     * @param syncError
     * @return
     */
    @Insert("insert into " + ConstantUtil.TABLE_SYNC_ERROR + "(block_height, block_hash, tx_hash, tx_num, error_info) values(#{blockHeight}, #{blockHash}, #{txHash}, #{txNum}, #{errorInfo})")
    Integer insertSyncError(SyncError syncError);
}
