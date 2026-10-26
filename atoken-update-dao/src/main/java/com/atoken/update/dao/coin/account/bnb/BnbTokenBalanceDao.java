package com.atoken.update.dao.coin.account.bnb;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.atoken.update.entity.coin.account.bnb.BnbTokenBalance;

@Mapper
public interface BnbTokenBalanceDao {

    @Select("SELECT * FROM coin_account.bnb_token_balance WHERE `contract_addr` = #{contractAddr} AND `addr` = #{addr}")
    BnbTokenBalance selectOneByContractAddrAndAddr(@Param("contractAddr") String contractAddr,
            @Param("addr") String addr);

    @Select("<script>SELECT * FROM coin_account.bnb_token_balance WHERE `contract_addr` = #{contractAddr} AND `addr` IN <foreach collection='addrs' open='(' item='addr' separator=',' close=')'> #{addr}</foreach></script>")
    List<BnbTokenBalance> selectListByContractAddrAndAddrs(
            @Param("contractAddr") String contractAddr, @Param("addrs") List<String> addrs);

    @Insert("INSERT INTO coin_account.bnb_token_balance (`contract_addr`, `addr`, `balance`, `decimals`, `added`) VALUES (#{contractAddr}, #{addr}, #{balance}, #{decimals}, #{added}) ON DUPLICATE KEY UPDATE `balance` = #{balance}")
    int saveOrUpdate(BnbTokenBalance tokenBalance);

    @Update("UPDATE coin_account.bnb_token_balance SET `balance` = #{balance} WHERE `id` = #{id}")
    int updateById(BnbTokenBalance tokenBalance);
}
