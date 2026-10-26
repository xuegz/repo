package com.atoken.update.dao.coin.account;


import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountAddressDao {
    String TABLE_COLUMN = "id, wallet_id walletId, addr, amount, platform, create_time createTime";

    /**
     * 通过地址查询钱包信息
     * @param coin
     * @param fromAddr
     * @param toAddr
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " where addr=#{fromAddr} union all " + " select " + TABLE_COLUMN + " from coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " where addr=#{toAddr}")
    List<Address> selectAddressByAddress(@Param("coin") String coin, @Param("fromAddr") String fromAddr, @Param("toAddr") String toAddr);

    /**
     * 通过地址更新余额信息
     * @param coin
     * @param address
     * @param amount
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " set amount=#{amount} where addr=#{address}")
    Integer updateAmountByAddress(@Param("coin") String coin, @Param("address") String address, @Param("amount") BigDecimal amount);


    /**
     * 通过地址查询钱包信息
     * @param coin
     * @param addr
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " where addr=#{addr}")
    List<Address> selectAddressByAddr(@Param("coin") String coin, @Param("addr") String addr);

}
