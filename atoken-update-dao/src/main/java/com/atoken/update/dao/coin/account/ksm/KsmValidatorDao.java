package com.atoken.update.dao.coin.account.ksm;

import com.atoken.update.entity.dot.DotValidator;
import com.atoken.update.entity.coin.account.ksm.KsmValidator;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KsmValidatorDao {

    final String TABLE_VALIDATOR_COLUMN = "id, addr, status, tokens, commission_rate commissionRate, annual_return annualReturn, all_delegator_num allDelegatorNum, order_num orderNum, keybase_url keybaseUrl, create_time createTime, update_time updateTime";

    /**
     * 查询验证节点列表
     * @return
     */
    @Select("select " + TABLE_VALIDATOR_COLUMN + " from coin_account.${coin}_" + ConstantUtil.TABLE_VALIDATOR+" where status = " + ConstantUtil.DOT_VALIDATOR_STATUS_BONDED)
    List<KsmValidator> selectBondedValidators(@Param("coin") String coin);

    /**
     * 通过验证人地址获取验证人信息
     * @param address
     * @return
     */
    @Select("select " + TABLE_VALIDATOR_COLUMN + " from coin_account.${coin}_"+ ConstantUtil.TABLE_VALIDATOR +" where addr = #{address}")
    KsmValidator selectValidatorByAddress(@Param("coin") String coin,@Param("address") String address);

    /**
     * 插入验证人信息
     * @param dotValidator
     * @return
     */
    @Insert("insert into coin_account.${coin}_" + ConstantUtil.TABLE_VALIDATOR + "(addr, status, tokens, commission_rate, annual_return, all_delegator_num, keybase_url) values(#{addr}, #{status}, #{tokens}, #{commissionRate}, #{annualReturn}, #{allDelegatorNum}, #{keybaseUrl})")
    Integer insertValidator(@Param("coin") String coin,DotValidator dotValidator);

    /**
     * 通过地址更新验证人信息
     * @param dotValidator
     * @return
     */
    @Update("update coin_account.${coin}_"+ ConstantUtil.TABLE_VALIDATOR +" set status = #{status}, tokens = #{tokens}, commission_rate = #{commissionRate}, annual_return = #{annualReturn}, all_delegator_num = #{allDelegatorNum} where addr = #{addr}")
    Integer updateValidatorByAddr(@Param("coin") String coin,DotValidator dotValidator);
}
