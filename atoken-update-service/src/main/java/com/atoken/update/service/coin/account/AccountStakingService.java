package com.atoken.update.service.coin.account;

import com.atoken.update.entity.coin.account.AmountInfo;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.entity.coin.account.staking.DelegateDetail;
import com.atoken.update.entity.coin.account.staking.ValidatorEO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountStakingService {
    Integer updateAmountByAddress(String coin, String address, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount);

    Integer updatePledgedByAddress(String coin, String address);

    List<ValidatorEO> selectBondedValidators(String coin);

    ValidatorEO selectValidatorByAddress(String coin, String address);

    Integer insertValidator(String coin, ValidatorEO validator);

    Integer updateValidatorByAddress(String coin, ValidatorEO validator);

    Integer insertUpdateValidator(String coin, ValidatorEO validator);

    List<Tx> selectUnbondedTxs(String coin, Long overTime);

    Integer updateAmountByAddress(String coin, String address, BigDecimal amount, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount);

    Integer updateTxStatus(String coin, Long id);

    void unbondingExpire(String coin, Long id, String address, String validatorAddress, BigDecimal amount, AmountInfo amountInfo, DelegateDetail delegateDetail);

    Integer insertOrUpdateDelegateDetail(String coin, DelegateDetail delegateDetail);

    Integer updateStakingStatus(String coin, DelegateDetail delegateDetail);

    Integer deleteDelegate(String coin, String addr, String validatorAddr);

    void unbondedExpireHandler(String coin, Tx tx, BigDecimal delegateAmount);

    Integer updateDelegateDetail(String coin, DelegateDetail delegateDetail);

}
