package com.atoken.update.dao.dot;

import com.atoken.update.dao.AddressDao;
import com.atoken.update.dao.TxDao;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotDelegateDetail;
import com.atoken.update.entity.dot.DotNominateExtrinsic;
import com.atoken.update.entity.dot.DotValidator;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DotDao {
    String VALIDATOR_VALIDATOR_COLUMN = "id, addr, status, tokens, commission_rate commissionRate, annual_return annualReturn, all_delegator_num allDelegatorNum, order_num orderNum, keybase_url keybaseUrl, points, create_time createTime, update_time updateTime";

    /**
     * 查询验证节点列表
     * @return
     */
    @Select("select " + VALIDATOR_VALIDATOR_COLUMN + " from dot_query.validator where status = " + ConstantUtil.DOT_VALIDATOR_STATUS_BONDED)
    List<DotValidator> selectBondedValidators();

    /**
     * 通过验证人地址获取验证人信息
     * @param address
     * @return
     */
    @Select("select " + VALIDATOR_VALIDATOR_COLUMN + " from dot_query.validator where addr = #{address}")
    DotValidator selectValidatorByAddress(String address);

    /**
     * 插入验证人信息
     * @param dotValidator
     * @return
     */
    @Insert("insert into dot_query.validator(addr, status, tokens, commission_rate, annual_return, all_delegator_num, keybase_url, points) values(#{addr}, #{status}, #{tokens}, #{commissionRate}, #{annualReturn}, #{allDelegatorNum}, #{keybaseUrl}, #{points})")
    Integer insertValidator(DotValidator dotValidator);

    /**
     * 通过地址更新验证人信息
     * @param dotValidator
     * @return
     */
    @Update("update dot_query.validator set status = #{status}, tokens = #{tokens}, commission_rate = #{commissionRate}, annual_return = #{annualReturn}, all_delegator_num = #{allDelegatorNum}, points = #{points} where addr = #{addr}")
    Integer updateValidatorByAddr(DotValidator dotValidator);



    /**
     * 查询解绑的交易
     * @param unbondHeight
     * @return
     */
    @Select("select "+ TxDao.TABLE_COLUMN +" from dot_query.tx where block_height < #{unbondHeight} and type = 'UNBOND' and status = 1")
    List<Tx> selectUnbondTxes(Integer unbondHeight);

    /**
     * 插入质押信息
     * @param dotDelegateDetail
     * @return
     */
    @Insert("insert into dot_query.delegate_detail(wallet_id, addr, validator_addr, delegate_amount) values(#{walletId}, #{addr}, #{validatorAddr}, #{delegateAmount})")
    Integer insertDelegateDetail(DotDelegateDetail dotDelegateDetail);

    /**
     * 通过地址删除质押信息
     * @param stashAddr
     * @return
     */
    @Delete("delete from dot_query.delegate_detail where addr=#{stashAddr}")
    Integer deleteDelegateByStashAddr(String stashAddr);

    /**
     * 更新地址质押金额
     * @param delegateAmount
     * @param address
     * @return
     */
    @Update("update dot_query.address set delegate_amount=#{delegateAmount} where addr=#{address}")
    Integer updateAddressDelegateAmount(@Param("delegateAmount")BigDecimal delegateAmount, @Param("address") String address);

    /**
     * 通过地址更新金额
     * @param address
     * @param amount
     * @param delegateAmount
     * @return
     */
    @Update("update dot_query.address set amount=#{amount}, delegate_amount=#{delegateAmount} where addr=#{address}")
    Integer updateAmountByAddress(@Param("address") String address, @Param("amount") BigDecimal amount, @Param("delegateAmount") BigDecimal delegateAmount);

    /**
     * 更新交易状态
     * @param id
     * @return
     */
    @Update("update dot_query.tx set status = 2 where id = #{id}")
    Integer updateTxStatus(Long id);

    /**
     * 更新质押状态
     * @param address
     * @param stakeStatus
     * @return
     */
    @Update("update dot_query.address set stake_status = #{stakeStatus} where addr = #{address}")
    Integer updateStakeStatus(@Param("address") String address, @Param("stakeStatus") String stakeStatus);

    /**
     * 查询提名列表
     * @return
     */
    @Select("select id, bond_hash bondHash, hash, extrinsic, status, error_message errorMessage, create_time createTime, update_time updateTime from dot_query.nominate_extrinsic where status = 'UNTREATED'")
    List<DotNominateExtrinsic> selectNominateExtrinsic();

    /**
     * 更新提名信息
     * @param dotNominateExtrinsic
     * @return
     */
    @Update("update dot_query.nominate_extrinsic set hash=#{hash}, status=#{status}, error_message=#{errorMessage} where id=#{id}")
    Integer updateExtrinsicStatus(DotNominateExtrinsic dotNominateExtrinsic);

    /**
     * 根据hash查询交易
     * @param hash
     * @return
     */
    @Select("select id, hash, state, status from dot_query.tx where hash = #{hash}")
    Tx selectTxByHash(String hash);

    /**
     * 查询质押地址列表
     * @return
     */
    @Select("select "+ AddressDao.TABLE_COLUMN +" from dot_query.address where stake_status = 'STAKED'")
    List<Address> selectStakedAddresses();

    /**
     * 查询控制者地址
     * @param walletId
     * @return
     */
    @Select("select addr from dot_query.address where wallet_id = #{walletId} and type = 'CONTROLLER'")
    String selectControllerAddress(String walletId);

    /**
     * 通过地址更新地质类型
     * @param address
     * @return
     */
    @Update("update dot_query.address set type='CONTROLLER' where addr=#{addr}")
    Integer updateAddressTypeByAddress(String address);
}
