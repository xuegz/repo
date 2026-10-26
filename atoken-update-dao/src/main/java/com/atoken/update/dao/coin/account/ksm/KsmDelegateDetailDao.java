package com.atoken.update.dao.coin.account.ksm;

import com.atoken.update.entity.coin.account.ksm.KsmDelegateDetail;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.*;

/**
 * @author Evil
 */
@Mapper
public interface KsmDelegateDetailDao {
    /**
     * 插入质押信息
     * @param ksmDelegateDetail
     * @return
     */
    @Insert("insert into coin_account.${coin}_"+ ConstantUtil.TABLE_DELEGATE_DETAIL+"(wallet_id, addr, validator_addr, delegate_amount) values(#{walletId}, #{addr}, #{validatorAddr}, #{delegateAmount})")
    Integer insertDelegateDetail(@Param("coin") String coin,@Param("ksmDelegateDetail") KsmDelegateDetail ksmDelegateDetail);

    /**
     * 通过地址删除质押信息
     * @param stashAddr
     * @return
     */
    @Delete("delete from coin_account.${coin}_"+ConstantUtil.TABLE_DELEGATE_DETAIL+" where addr=#{stashAddr}")
    Integer deleteDelegateByStashAddr(@Param("coin") String coin,@Param("stashAddr") String stashAddr);
}
