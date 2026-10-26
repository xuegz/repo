package com.atoken.update.dao.atom;

import com.atoken.update.entity.atom.AtomDelegateDetail;
import com.atoken.update.entity.atom.AtomTx;
import com.atoken.update.entity.atom.AtomValidatorEO;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AtomDao {
    String VALIDATOR_VALIDATOR_COLUMN = "id, addr, consensus_pubkey consensusPubkey, jailed, status, tokens, delegator_shares delegatorShares, moniker, identity, website, details, unbonding_height unbondingHeight, unbonding_timestamp unbondingTimestamp, rate, max_rate maxRate, max_change_rate maxChangeRate, rate_update_timestamp rateUpdateTimestamp, year_yield yearYield, all_delegator_num allDelegatorNum, order_num orderNum, keybase_url keybaseUrl, create_time createTime, update_time updateTime";
    String TABLE_VALIDATOR = "validator";

    /**
     * 通过地址更新余额
     * @param address
     * @param delegateAmount
     * @param unbondingAmount
     * @param rewardsAmount
     * @return
     */
    @Update("update atom_query." + ConstantUtil.TBALE_ADDRESS + " set delegate_amount=#{delegateAmount}, unbonding_amount=#{unbondingAmount}, rewards_amount=#{rewardsAmount} where addr=#{address}")
    Integer updateAmountByAddress(@Param("address") String address, @Param("delegateAmount") BigDecimal delegateAmount, @Param("unbondingAmount") BigDecimal unbondingAmount, @Param("rewardsAmount") BigDecimal rewardsAmount);

    /**
     * 通过地址查询账户
     * @param address
     * @return
     */
    @Select("select account_num accountNum from atom_query." + ConstantUtil.TBALE_ADDRESS + " where addr=#{address} limit 1")
    String selectAccountNumByAddress(String address);

    /**
     * 更新交易的account_num
     * @param id
     * @param accountNum
     * @return
     */
    @Update("update atom_query.tx set account_num=#{accountNum} where id=#{id}")
    Integer updateTxAccountNum(@Param("id") Long id, @Param("accountNum") String accountNum);

    /**
     * 查询验证节点列表
     * @return
     */
    @Select("select " + VALIDATOR_VALIDATOR_COLUMN + " from atom_query." + TABLE_VALIDATOR + " where status = " + ConstantUtil.ATOM_VALIDATOR_STATUS_BONDED)
    List<AtomValidatorEO> selectBondedValidators();

    /**
     * 通过验证人地址获取验证人信息
     * @param address
     * @return
     */
    @Select("select " + VALIDATOR_VALIDATOR_COLUMN + " from atom_query." + TABLE_VALIDATOR + " where addr=#{address}")
    AtomValidatorEO selectValidatorByAddress(String address);

    /**
     * 插入验证人信息
     * @param atomValidator
     * @return
     */
    @Insert("insert into atom_query.validator(addr, consensus_pubkey, jailed, status, tokens, delegator_shares, moniker, identity, website, details, unbonding_height, unbonding_timestamp, rate, max_rate, max_change_rate, rate_update_timestamp, year_yield, all_delegator_num, keybase_url) values(#{addr}, #{consensusPubkey}, #{jailed}, #{status}, #{tokens}, #{delegatorShares}, #{moniker}, #{identity}, #{website}, #{details}, #{unbondingHeight}, #{unbondingTimestamp}, #{rate}, #{maxRate}, #{maxChangeRate}, #{rateUpdateTimestamp}, #{yearYield}, #{allDelegatorNum}, #{keybaseUrl})")
    Integer insertValidator(AtomValidatorEO atomValidator);

    /**
     * 通过地址更新验证人信息
     * @param atomValidator
     * @return
     */
    @Update("UPDATE atom_query.validator set consensus_pubkey=#{consensusPubkey}, jailed=#{jailed}, status=#{status}, tokens=#{tokens}, delegator_shares=#{delegatorShares}, moniker=#{moniker}, identity=#{identity}, website=#{website}, details=#{details}, unbonding_height=#{unbondingHeight}, unbonding_timestamp=#{unbondingTimestamp}, rate=#{rate}, max_rate=#{maxRate}, max_change_rate=#{maxChangeRate}, rate_update_timestamp=#{rateUpdateTimestamp}, year_yield=#{yearYield}, all_delegator_num=#{allDelegatorNum}, keybase_url=#{keybaseUrl} where addr=#{addr}")
    Integer updateValidatorByAddress(AtomValidatorEO atomValidator);

    /**
     * 插入或更新验证人信息
     * @param atomValidator
     * @return
     */
    @Insert("insert into atom_query.validator(addr, consensus_pubkey, jailed, status, tokens, delegator_shares, moniker, identity, website, details, unbonding_height, unbonding_timestamp, rate, max_rate, max_change_rate, rate_update_timestamp, year_yield, all_delegator_num, keybase_url) values(#{addr}, #{consensusPubkey}, #{jailed}, #{status}, #{tokens}, #{delegatorShares}, #{moniker}, #{identity}, #{website}, #{details}, #{unbondingHeight}, #{unbondingTimestamp}, #{rate}, #{maxRate}, #{maxChangeRate}, #{rateUpdateTimestamp}, #{yearYield}, #{allDelegatorNum}, #{keybaseUrl}) on duplicate KEY UPDATE consensus_pubkey=#{consensusPubkey}, jailed=#{jailed}, status=#{status}, tokens=#{tokens}, delegator_shares=#{delegatorShares}, moniker=#{moniker}, identity=#{identity}, website=#{website}, details=#{details}, unbonding_height=#{unbondingHeight}, unbonding_timestamp=#{unbondingTimestamp}, rate=#{rate}, max_rate=#{maxRate}, max_change_rate=#{maxChangeRate}, rate_update_timestamp=#{rateUpdateTimestamp}, year_yield=#{yearYield}, all_delegator_num=#{allDelegatorNum}, keybase_url=#{keybaseUrl}")
    Integer insertUpdateValidator(AtomValidatorEO atomValidator);

    /**
     * 查询解绑的交易
     * @param currentTime
     * @param unbondingTime
     * @return
     */
    @Select("select id, hash, from_addr fromAddr, to_addr toAddr,state, timestamp from atom_query.tx where  #{currentTime} - timestamp > #{unbondingTime} and state = 1 and type='cosmos-sdk/MsgUndelegate' and status = 1")
    List<AtomTx> selectUnbondedAtomTxs(@Param("currentTime") Long currentTime, @Param("unbondingTime") Long unbondingTime);

    /**
     * 通过地址更新各种余额
     * @param address
     * @param amount
     * @param delegateAmount
     * @param unbondingAmount
     * @param rewardsAmount
     * @return
     */
    @Update("update atom_query." + ConstantUtil.TBALE_ADDRESS + " set amount=#{amount}, delegate_amount=#{delegateAmount}, unbonding_amount=#{unbondingAmount}, rewards_amount=#{rewardsAmount} where addr=#{address}")
    Integer updateAllAmountByAddress(@Param("address") String address, @Param("amount") BigDecimal amount, @Param("delegateAmount") BigDecimal delegateAmount, @Param("unbondingAmount") BigDecimal unbondingAmount, @Param("rewardsAmount") BigDecimal rewardsAmount);

    /**
     * 更新交易状态
     * @param id
     * @return
     */
    @Update("update atom_query.tx set status = 2 where id = #{id}")
    Integer updateTxStatus(Long id);

    /**
     * 插入或者更新质押信息
     * @param atomDelegateDetail
     * @return
     */
    @Insert("insert into atom_query.delegate_detail(wallet_id, addr, validator_addr, delegate_amount, unbonding_amount) values(#{walletId}, #{addr}, #{validatorAddr}, #{delegateAmount}, #{unbondingAmount}) ON DUPLICATE key update delegate_amount = #{delegateAmount}, unbonding_amount = #{unbondingAmount}")
    Integer insertOrUpdateDelegateDetail(AtomDelegateDetail atomDelegateDetail);

    /**
     * 更新钱包的质押信息
     * @param atomDelegateDetail
     * @return
     */
    @Update("update atom_query.delegate_detail set delegate_amount=#{delegateAmount}, unbonding_amount=#{unbondingAmount} where addr=#{addr} and validator_addr=#{validatorAddr}")
    Integer updateDelegateDetail(AtomDelegateDetail atomDelegateDetail);

    /**
     * 通过地址查询钱包id
     * @param address
     * @return
     */
    @Select("select wallet_id walletId from atom_query." + ConstantUtil.TBALE_ADDRESS + " where addr = #{addr}")
    String selectWalletIdByAddress(String address);
}
