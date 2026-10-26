package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.Address;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OntAddressDao {
    String TABLE_COLUMN = "id, wallet_id walletId, addr, platform, create_time createTime";

    /**
     * 通过地址查询地址信息
     * @param fromAddr
     * @param toAddr
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from " + ConstantUtil.TBALE_ADDRESS + " where addr=#{fromAddr} union all " + " select " + TABLE_COLUMN + " from " + ConstantUtil.TBALE_ADDRESS + " where addr=#{toAddr}")
    List<Address> selectAddressByAddress(@Param("fromAddr") String fromAddr, @Param("toAddr") String toAddr);
}
