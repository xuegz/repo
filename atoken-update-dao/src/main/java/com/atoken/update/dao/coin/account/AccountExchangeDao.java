package com.atoken.update.dao.coin.account;

import com.atoken.update.entity.coin.account.CoinFee;
import com.atoken.update.entity.coin.account.ImportAddressBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccountExchangeDao {
    /**
     * 更新币种fee信息
     * @param coinFee
     * @return
     */
    @Update("update exchange.coin_fee set block_tx_num = #{blockTxNum}, block_size=#{blockSize},mempool_tx_num=#{mempoolTxNum}, update_time=now() where symbol=#{symbol}")
    Integer updateCionFee(CoinFee coinFee);

    /**
     * 插入币种fee信息
     * @param coinFee
     * @return
     */
    @Insert("insert ignore into `exchange`.`coin_fee`(symbol, rate, block_tx_num, block_size, mempool_tx_num) values(#{symbol}, #{rate}, #{blockTxNum}, #{blockSize}, #{mempoolTxNum})")
    Integer insertCoinFee(CoinFee coinFee);

    /**
     * 根据币种类型获取导入地址信息
     * @param coinType
     * @return
     */
    @Select("select id, wallet_id walletId, coin_type coinType, addr, status, del, count, create_time createTime, update_time updateTime from exchange.import_address where coin_type=#{coinType} and status = 0 and del = 0 order by count asc limit 100")
    List<ImportAddressBean> selectImportAddressByCoinType(String coinType);

    /**
     * 更新导入地址表状态
     * @param status
     * @param count
     * @param id
     * @return
     */
    @Update("update exchange.import_address set status = #{status}, count = #{count}, update_time = current_timestamp where id = #{id}")
    Integer updateImportAddressStatus(@Param("status") Integer status, @Param("count") Integer count, @Param("id") Integer id);
}
