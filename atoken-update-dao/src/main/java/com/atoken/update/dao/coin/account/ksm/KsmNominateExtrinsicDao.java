package com.atoken.update.dao.coin.account.ksm;

import com.atoken.update.entity.dot.DotNominateExtrinsic;
import com.atoken.update.entity.coin.account.ksm.KsmNominateExtrinsic;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface KsmNominateExtrinsicDao {

    final String TABLE_COLUMN = "id, bond_hash bondHash, hash, extrinsic, status, error_message errorMessage, create_time createTime, update_time updateTime";

    /**
     * 查询提名列表
     * @return
     */
    @Select("select "+TABLE_COLUMN+" from coin_account.${coin}_"+ ConstantUtil.TABLE_NOMINATE_EXTRINSIC +" where status = 'UNTREATED'")
    List<KsmNominateExtrinsic> selectNominateExtrinsic(String coin);

    /**
     * 更新提名信息
     * @param ksmNominateExtrinsic
     * @return
     */
    @Update("update coin_account.${coin}_"+ConstantUtil.TABLE_NOMINATE_EXTRINSIC+" set hash=#{hash}, status=#{status}, error_message=#{errorMessage} where id=#{id}")
    Integer updateExtrinsicStatus(@Param("coin") String coin,@Param("ksmNominateExtrinsic") KsmNominateExtrinsic ksmNominateExtrinsic);
}
