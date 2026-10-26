package com.atoken.update.service.coin.account.ksm;

import com.atoken.update.entity.Address;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.coin.account.ksm.KsmDelegateDetail;
import com.atoken.update.entity.coin.account.ksm.KsmNominateExtrinsic;
import com.atoken.update.entity.coin.account.ksm.KsmValidator;
import com.atoken.update.entity.dot.DotValidator;

import java.math.BigDecimal;
import java.util.List;

public interface KsmService {

    List<KsmValidator> selectBondedValidators(String coin);

    Integer insertValidator(String coin,KsmValidator ksmValidator);

    Integer updateAmountByAddress(String coin,String address, BigDecimal amount, BigDecimal delegateAmount);

    List<KsmNominateExtrinsic> selectNominateExtrinsic(String coin);

    Integer updateExtrinsicStatus(String coin, KsmNominateExtrinsic ksmNominateExtrinsic);

    List<Address> selectStakedAddresses(String coin);

    String selectControllerAddress(String coin,String walletId);

    Integer updateAddressDelegateAmount(String coin,BigDecimal delegateAmount, String address);

    Integer insertDelegateDetail(String coin, KsmDelegateDetail ksmDelegateDetail);

    Integer updateStakeStatus(String coin,String address, String stakeStatus);

    Integer updateTxStatus(String coin,Long id);

    Integer deleteDelegateByStashAddr(String coin,String stashAddr);

    Integer updateAddressTypeByAddress(String coin,String address);

    Tx selectTxByHash(String coin,String hash);

    List<Tx> selectUnbondTxes(String coin,Integer unbondHeight);

    KsmValidator selectValidatorByAddress(String coin,String address);

    Integer updateValidatorByAddr(String coin,DotValidator dotValidator);
}
