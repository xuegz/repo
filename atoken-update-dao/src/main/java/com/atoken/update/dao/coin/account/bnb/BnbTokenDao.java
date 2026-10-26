package com.atoken.update.dao.coin.account.bnb;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.atoken.update.entity.coin.account.bnb.BnbToken;

@Mapper
public interface BnbTokenDao {

    @Select("SELECT * FROM coin_account.bnb_token WHERE contract_addr = #{contractAddr}")
    BnbToken selectOneByContractAddr(@Param("contractAddr") String contractAddr);

    @Insert("INSERT INTO coin_account.bnb_token (`contract_addr`, `coin_name`, `coin_full_name`, `decimals`, `logo_url`) VALUES (#{contractAddr}, #{coinName}, #{coinFullName}, #{decimals}, #{logoUrl}) ON DUPLICATE KEY UPDATE `coin_full_name` = #{coinFullName}")
    int saveOrUpdate(BnbToken token);
}
