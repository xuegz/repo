package com.atoken.update.dao;


import com.atoken.update.entity.Address;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AddressDao {
    String TABLE_COLUMN = "id, wallet_id walletId, addr, amount, platform, create_time createTime";

    /**
     * 通过地址查询钱包信息
     * @param dataBase
     * @param fromAddr
     * @param toAddr
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from ${dataBase}." + ConstantUtil.TBALE_ADDRESS + " where addr=#{fromAddr} union all " + " select " + TABLE_COLUMN + " from ${dataBase}." + ConstantUtil.TBALE_ADDRESS + " where addr=#{toAddr}")
    List<Address> selectAddressByAddress(@Param("dataBase") String dataBase, @Param("fromAddr") String fromAddr, @Param("toAddr") String toAddr);


    /**
     * 通过地址更新余额信息
     * @param dataBase
     * @param address
     * @param amount
     * @return
     */
    @Update("update ${dataBase}." + ConstantUtil.TBALE_ADDRESS + " set amount=#{amount} where addr=#{address}")
    Integer updateAmountByAddress(@Param("dataBase") String dataBase, @Param("address") String address, @Param("amount") BigDecimal amount);

}
