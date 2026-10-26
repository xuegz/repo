package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.AddressAsset;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OntAddressAssetDao {
    /**
     * 插入地址资产
     * @param addressAsset
     * @return
     */
    @Insert("insert into " + ConstantUtil.TABLE_ADDRESS_ASSET + "(contract_addr, asset_code, addr, wallet_id) values(#{contractAddr}, #{assetCode}, #{addr}, #{walletId})")
    Integer insertAddressAsset(AddressAsset addressAsset);

    /**
     * 地址资产是否存在
     * @param addr
     * @param contractAddr
     * @return
     */
    @Select("select count(1) from " + ConstantUtil.TABLE_ADDRESS_ASSET + " where addr=#{addr} and contract_addr=#{contractAddr}")
    Integer existAddressAsset(@Param("addr") String addr, @Param("contractAddr") String contractAddr);

    /**
     * 更新地址资产
     * @param addressAsset
     * @return
     */
    @Update("update " + ConstantUtil.TABLE_ADDRESS_ASSET + " set amount=amount+#{amount} where addr=#{addr} and contract_addr=#{contractAddr} and amount+#{amount}>=0")
    Integer updateAddressAsset(AddressAsset addressAsset);

    /**
     * 更新资产
     * @param addressAsset
     * @return
     */
    @Update("update " + ConstantUtil.TABLE_ADDRESS_ASSET + " set amount=#{amount} where addr=#{addr} and contract_addr=#{contractAddr}")
    Integer updateAsset(AddressAsset addressAsset);

    /**
     * 更新主网资产
     * @param addressAsset
     * @return
     */
    @Update("update " + ConstantUtil.TABLE_ADDRESS_ASSET + " set amount=#{amount} where addr=#{addr} and asset_type='" + ConstantUtil.ASSERT_TYPE_MAIN + "' and amount+#{amount}>=0")
    Integer updateMainAsset(AddressAsset addressAsset);
}
