package com.atoken.update.service.atom;

import com.atoken.update.entity.atom.AtomAmountInfo;
import com.atoken.update.entity.atom.AtomDelegateDetail;
import com.atoken.update.entity.atom.AtomTx;
import com.atoken.update.entity.atom.AtomValidatorEO;

import java.math.BigDecimal;
import java.util.List;

public interface AtomService {
    Integer updateAmountByAddress(String address, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount);

    String selectAccountNumByAddress(String address);

    Integer updateTxAccountNum(Long id, String accountNum);

    List<AtomValidatorEO> selectBondedValidators();

    AtomValidatorEO selectValidatorByAddress(String address);

    Integer insertValidator(AtomValidatorEO atomValidator);

    Integer updateValidatorByAddress(AtomValidatorEO atomValidator);

    Integer insertUpdateValidator(AtomValidatorEO atomValidator);

    List<AtomTx> selectUnbondedAtomTxs(Long currentTime, Long unbondingTime);

    Integer updateAmountByAddress(String address, BigDecimal amount, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount);

    Integer updateTxStatus(Long id);

    void unbondingExpire(Long id, String address, String validatorAddress, BigDecimal amount, AtomAmountInfo atomAmountInfo, AtomDelegateDetail atomDelegateDetail);

    Integer insertOrUpdateDelegateDetail(AtomDelegateDetail atomDelegateDetail);

    Integer updateDelegateDetail(AtomDelegateDetail atomDelegateDetail);

    String selectWalletIdByAddress(String address);
}
