package com.atoken.update.dao.coin.account.ht;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.atoken.update.entity.coin.account.ht.HtTx;

@Mapper
public interface HtTxDao {

    @Insert("INSERT INTO coin_account.ht_tx (`tx_hash`, `from_addr`, `to_addr`, `amount`, `nonce`, `timestamp`, `gas`, `gas_used`, `gas_price`, `fee`, `state`, `status`, `block_hash`, `block_height`) "
            + "VALUES (#{txHash}, #{fromAddr}, #{toAddr}, #{amount}, #{nonce}, #{timestamp}, #{gas}, #{gasUsed}, #{gasPrice}, #{fee}, #{state}, #{status}, #{blockHash}, #{blockHeight}) ON DUPLICATE KEY UPDATE `gas_used` = #{gasUsed}, `fee` = #{fee}, `state` = #{state}, `status` = #{status}, `block_hash` = #{blockHash}, `block_height` = #{blockHeight}")
    int saveOrUpdate(HtTx tx);

    @Select("SELECT * FROM coin_account.ht_tx WHERE `block_hash` = #{blockHash}")
    List<HtTx> selectListByBlockHash(@Param("blockHash") String blockHash);

    @Delete("<script>DELETE FROM coin_account.ht_tx WHERE `id` IN <foreach collection='ids' open='(' item='id' separator=',' close=')'> #{id}</foreach></script>")
    int deleteByIds(@Param("ids") List<Long> ids);
}
