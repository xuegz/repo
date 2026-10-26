package com.atoken.update.dao.coin.account.ksm;
import com.atoken.update.entity.Address;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface KsmAddressDao{

    final String ADDRESS_COLUMN = "id,wallet_id walletId,addr,amount,delegate_amount delegateAmount,unbonding_amount unbondingAmount,rewards_amount rewardsAmount,platform,addr_type addrType,account_num accountNum,create_time createTime,update_time updateTime,type,stake_status stakeStatus,appId,app_id appId";

    /**
     * 通过地址更新地质类型
     * @param address
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " set type='CONTROLLER' where addr=#{address}")
    Integer updateAddressTypeByAddress(@Param("coin") String coin,@Param("address") String address);

    /**
     * 更新地址质押金额
     * @param delegateAmount
     * @param address
     * @return
     */
    @Update("update coin_account.${coin}_"+ ConstantUtil.TBALE_ADDRESS +" set delegate_amount=#{delegateAmount} where addr=#{address}")
    Integer updateAddressDelegateAmount(@Param("coin")String coin,@Param("delegateAmount") BigDecimal delegateAmount, @Param("address") String address);

    /**
     * 通过地址更新金额
     * @param address
     * @param amount
     * @param delegateAmount
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " set amount=#{amount}, delegate_amount=#{delegateAmount} where addr=#{address}")
    Integer updateAmountByAddress(@Param("coin") String coin,@Param("address") String address, @Param("amount") BigDecimal amount, @Param("delegateAmount") BigDecimal delegateAmount);

    /**
     * 更新质押状态
     * @param address
     * @param stakeStatus
     * @return
     */
    @Update("update coin_account.${coin}_"+ ConstantUtil.TBALE_ADDRESS +" set stake_status = #{stakeStatus} where addr = #{address}")
    Integer updateStakeStatus(@Param("coin") String coin,@Param("address") String address, @Param("stakeStatus") String stakeStatus);

    /**
     * 查询质押地址列表
     * @return
     */
    @Select("select "+ ADDRESS_COLUMN +" from coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " where stake_status = 'STAKED'")
    List<Address> selectStakedAddresses(@Param("coin") String coin);

    /**
     * 查询控制者地址
     * @param walletId
     * @return
     */
    @Select("select addr from coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " where wallet_id = #{walletId} and type = 'CONTROLLER'")
    String selectControllerAddress(@Param("coin") String coin,@Param("walletId") String walletId);
}
