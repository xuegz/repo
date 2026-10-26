package com.atoken.update.dao.coin.account.ht;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.atoken.update.entity.coin.account.ht.HtToken;

@Mapper
public interface HtTokenDao {

    @Select("SELECT * FROM coin_account.ht_token WHERE contract_addr = #{contractAddr}")
    HtToken selectOneByContractAddr(@Param("contractAddr") String contractAddr);

    @Insert("INSERT INTO coin_account.ht_token (`contract_addr`, `coin_name`, `coin_full_name`, `decimals`, `logo_url`) VALUES (#{contractAddr}, #{coinName}, #{coinFullName}, #{decimals}, #{logoUrl}) ON DUPLICATE KEY UPDATE `coin_full_name` = #{coinFullName}")
    int saveOrUpdate(HtToken token);
}
