package com.atoken.update.dao.ont;

import com.atoken.update.entity.ont.Token;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OntTokenDao {
    String TABLE_COLUMN = "id, name, full_name fullName, contract_addr contractAddr, asset_code assetCode, decimals, can_shift canShift, logo, create_time createTime, update_time updateTime, asset_type assetType, del";

    /**
     * 查询token列表
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from token where del=" + ConstantUtil.TABLE_UNDELETED)
    List<Token> selectTokes();

    /**
     * 通过地址类型查询token信息
     * @param assetType
     * @return
     */
    @Select("select " + TABLE_COLUMN + " from token where del=" + ConstantUtil.TABLE_UNDELETED + " and asset_type=#{assetType}")
    Token selectTokenByAssetType(String assetType);
}
