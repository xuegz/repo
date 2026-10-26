package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.CoinFee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OntExchangeDao {
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
}
