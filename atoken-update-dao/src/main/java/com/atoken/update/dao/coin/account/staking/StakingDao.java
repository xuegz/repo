package com.atoken.update.dao.coin.account.staking;

import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.entity.coin.account.staking.DelegateDetail;
import com.atoken.update.entity.coin.account.staking.ValidatorEO;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface StakingDao {
    String VALIDATOR_VALIDATOR_COLUMN = "id, addr, consensus_pubkey consensusPubkey, jailed, status, tokens, delegator_shares delegatorShares, moniker, identity, website, details, unbonding_height unbondingHeight, unbonding_timestamp unbondingTimestamp, rate, max_rate maxRate, max_change_rate maxChangeRate, rate_update_timestamp rateUpdateTimestamp, year_yield yearYield, all_delegator_num allDelegatorNum, order_num orderNum, keybase_url keybaseUrl, create_time createTime, update_time updateTime";
    String TABLE_VALIDATOR = "validator";

    /**
     * 通过地址更新余额
     * @param coin
     * @param address
     * @param delegateAmount
     * @param unbondingAmount
     * @param rewardsAmount
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " set delegate_amount=#{delegateAmount}, unbonding_amount=#{unbondingAmount}, rewards_amount=#{rewardsAmount} where addr=#{address}")
    Integer updateAmountByAddress(@Param("coin") String coin, @Param("address") String address, @Param("delegateAmount") BigDecimal delegateAmount, @Param("unbondingAmount") BigDecimal unbondingAmount, @Param("rewardsAmount") BigDecimal rewardsAmount);

    /**
     * 通过地址更新质押状态
     * @param coin
     * @param address
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " set pledged=1 where addr=#{address}")
    Integer updatePledgedByAddress(@Param("coin") String coin, @Param("address") String address);

    /**
     * 查询验证人列表
     * @param coin
     * @return
     */
    @Select("select " + VALIDATOR_VALIDATOR_COLUMN + " from coin_account.${coin}_" + TABLE_VALIDATOR + " where status = " + ConstantUtil.ATOM_VALIDATOR_STATUS_BONDED)
    List<ValidatorEO> selectBondedValidators(@Param("coin") String coin);

    /**
     * 通过地址获取验证人信息
     * @param coin
     * @param address
     * @return
     */
    @Select("select " + VALIDATOR_VALIDATOR_COLUMN + " from coin_account.${coin}_" + TABLE_VALIDATOR + " where addr=#{address}")
    ValidatorEO selectValidatorByAddress(@Param("coin") String coin, @Param("address") String address);

    /**
     * 插入验证人信息
     * @param coin
     * @param validator
     * @return
     */
    @Insert("insert into coin_account.${coin}_validator(addr, consensus_pubkey, jailed, status, tokens, delegator_shares, moniker, identity, website, details, unbonding_height, unbonding_timestamp, rate, max_rate, max_change_rate, rate_update_timestamp, year_yield, all_delegator_num, keybase_url) values(#{validator.addr}, #{validator.consensusPubkey}, #{validator.jailed}, #{validator.status}, #{validator.tokens}, #{validator.delegatorShares}, #{validator.moniker}, #{validator.identity}, #{validator.website}, #{validator.details}, #{validator.unbondingHeight}, #{validator.unbondingTimestamp}, #{validator.rate}, #{validator.maxRate}, #{validator.maxChangeRate}, #{validator.rateUpdateTimestamp}, #{validator.yearYield}, #{validator.allDelegatorNum}, #{validator.keybaseUrl})")
    Integer insertValidator(@Param("coin") String coin, @Param("validator") ValidatorEO validator);

    /**
     * 通过地址更细验证人信息
     * @param coin
     * @param validator
     * @return
     */
    @Update("UPDATE coin_account.${coin}_validator set consensus_pubkey=#{validator.consensusPubkey}, jailed=#{validator.jailed}, status=#{validator.status}, tokens=#{validator.tokens}, delegator_shares=#{validator.delegatorShares}, moniker=#{validator.moniker}, identity=#{validator.identity}, website=#{validator.website}, details=#{validator.details}, unbonding_height=#{validator.unbondingHeight}, unbonding_timestamp=#{validator.unbondingTimestamp}, rate=#{validator.rate}, max_rate=#{validator.maxRate}, max_change_rate=#{validator.maxChangeRate}, rate_update_timestamp=#{validator.rateUpdateTimestamp}, year_yield=#{validator.yearYield}, all_delegator_num=#{validator.allDelegatorNum}, keybase_url=#{validator.keybaseUrl} where addr=#{validator.addr}")
    Integer updateValidatorByAddress(@Param("coin") String coin, @Param("validator") ValidatorEO validator);

    /**
     * 插入或者更新验证人信息
     * @param coin
     * @param validator
     * @return
     */
    @Insert("insert into coin_account.${coin}_validator(addr, consensus_pubkey, jailed, status, tokens, delegator_shares, moniker, identity, website, details, unbonding_height, unbonding_timestamp, rate, max_rate, max_change_rate, rate_update_timestamp, year_yield, all_delegator_num, keybase_url) values(#{validator.addr}, #{validator.consensusPubkey}, #{validator.jailed}, #{validator.status}, #{validator.tokens}, #{validator.delegatorShares}, #{validator.moniker}, #{validator.identity}, #{validator.website}, #{validator.details}, #{validator.unbondingHeight}, #{validator.unbondingTimestamp}, #{validator.rate}, #{validator.maxRate}, #{validator.maxChangeRate}, #{validator.rateUpdateTimestamp}, #{validator.yearYield}, #{validator.allDelegatorNum}, #{validator.keybaseUrl}) on duplicate KEY UPDATE consensus_pubkey=#{validator.consensusPubkey}, jailed=#{validator.jailed}, status=#{validator.status}, tokens=#{validator.tokens}, delegator_shares=#{validator.delegatorShares}, moniker=#{validator.moniker}, identity=#{validator.identity}, website=#{validator.website}, details=#{validator.details}, unbonding_height=#{validator.unbondingHeight}, unbonding_timestamp=#{validator.unbondingTimestamp}, rate=#{validator.rate}, max_rate=#{validator.maxRate}, max_change_rate=#{validator.maxChangeRate}, rate_update_timestamp=#{validator.rateUpdateTimestamp}, year_yield=#{validator.yearYield}, all_delegator_num=#{validator.allDelegatorNum}, keybase_url=#{validator.keybaseUrl}")
    Integer insertUpdateValidator(@Param("coin") String coin, @Param("validator") ValidatorEO validator);

    /**
     * 查询解质押的交易
     * @param coin
     * @param overTime
     * @return
     */
    @Select("select id, hash, from_addr fromAddr, to_addr toAddr,state, timestamp from coin_account.${coin}_tx where  timestamp < #{overTime} and state = 1 and type='UNVOTE' and status = 1")
    List<Tx> selectUnbondedTxs(@Param("coin") String coin, @Param("overTime") Long overTime);

    /**
     * 通过地址更新余额信息
     * @param coin
     * @param address
     * @param amount
     * @param delegateAmount
     * @param unbondingAmount
     * @param rewardsAmount
     * @return
     */
    @Update("update coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " set amount=#{amount}, delegate_amount=#{delegateAmount}, unbonding_amount=#{unbondingAmount}, rewards_amount=#{rewardsAmount} where addr=#{address}")
    Integer updateAllAmountByAddress(@Param("coin") String coin, @Param("address") String address, @Param("amount") BigDecimal amount, @Param("delegateAmount") BigDecimal delegateAmount, @Param("unbondingAmount") BigDecimal unbondingAmount, @Param("rewardsAmount") BigDecimal rewardsAmount);

    /**
     * 更新交易状态
     * @param coin
     * @param id
     * @return
     */
    @Update("update coin_account.${coin}_tx set status = 2 where id = #{id}")
    Integer updateTxStatus(@Param("coin") String coin, @Param("id") Long id);

    /**
     * 插入或者更新质押信息
     * @param coin
     * @param delegateDetail
     * @return
     */
    @Insert("insert into coin_account.${coin}_delegate_detail(wallet_id, addr, validator_addr, delegate_amount, unbonding_amount) values(#{delegateDetail.walletId}, #{delegateDetail.addr}, #{delegateDetail.validatorAddr}, #{delegateDetail.delegateAmount}, #{delegateDetail.unbondingAmount}) ON DUPLICATE key update delegate_amount = #{delegateDetail.delegateAmount}, unbonding_amount = #{delegateDetail.unbondingAmount}")
    Integer insertOrUpdateDelegateDetail(@Param("coin") String coin, @Param("delegateDetail") DelegateDetail delegateDetail);

    /**
     * 更新质押信息
     * @param coin
     * @param delegateDetail
     * @return
     */
    @Update("update coin_account.${coin}_delegate_detail set delegate_amount=#{delegateDetail.delegateAmount}, unbonding_amount=#{delegateDetail.unbondingAmount} where addr=#{delegateDetail.addr} and validator_addr=#{delegateDetail.validatorAddr}")
    Integer updateDelegateDetail(@Param("coin") String coin, @Param("delegateDetail") DelegateDetail delegateDetail);

    /**
     * 更新质押状态
     * @param coin
     * @param delegateDetail
     * @return
     */
    @Update("update coin_account.${coin}_delegate_detail set status=#{delegateDetail.status} where addr=#{delegateDetail.addr} and validator_addr=#{delegateDetail.validatorAddr}")
    Integer updateStakingStatus(@Param("coin") String coin, @Param("delegateDetail") DelegateDetail delegateDetail);

    /**
     * 删除质押信息
     * @param coin
     * @param addr
     * @param validatorAddr
     * @return
     */
    @Delete("delete from coin_account.${coin}_delegate_detail where addr=#{addr} and validator_addr=#{validatorAddr}")
    Integer deleteDelegate(@Param("coin") String coin, @Param("addr") String addr, @Param("validatorAddr") String validatorAddr);

    /**
     * 通过地址查询钱包id
     * @param coin
     * @param address
     * @return
     */
    @Select("select wallet_id walletId from coin_account.${coin}_" + ConstantUtil.TBALE_ADDRESS + " where addr = #{addr}")
    String selectWalletIdByAddress(@Param("coin") String coin, @Param("address") String address);
}
