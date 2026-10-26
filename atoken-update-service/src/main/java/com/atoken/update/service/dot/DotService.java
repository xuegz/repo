package com.atoken.update.service.dot;

import com.atoken.update.entity.Address;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotDelegateDetail;
import com.atoken.update.entity.dot.DotNominateExtrinsic;
import com.atoken.update.entity.dot.DotValidator;

import java.math.BigDecimal;
import java.util.List;

public interface DotService {
    List<DotValidator> selectBondedValidators();

    DotValidator selectValidatorByAddress(String address);

    Integer insertValidator(DotValidator dotValidator);

    Integer updateValidatorByAddr(DotValidator dotValidator);

    List<Tx> selectUnbondTxes(Integer unbondHeight);

    Integer insertDelegateDetail(DotDelegateDetail dotDelegateDetail);

    Integer deleteDelegateByStashAddr(String stashAddr);

    Integer updateAddressDelegateAmount(BigDecimal delegateAmount, String address);

    Integer updateAmountByAddress(String address, BigDecimal amount, BigDecimal delegateAmount);

    Integer updateTxStatus(Long id);

    Integer updateStakeStatus(String address, String stakeStatus);

    List<DotNominateExtrinsic> selectNominateExtrinsic();

    Integer updateExtrinsicStatus(DotNominateExtrinsic dotNominateExtrinsic);

    Tx selectTxByHash(String hash);

    List<Address> selectStakedAddresses();

    String selectControllerAddress(String walletId);

    Integer updateAddressTypeByAddress(String address);
}
