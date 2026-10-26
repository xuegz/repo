package com.atoken.update.service.coin.account.ksm.impl;

import com.atoken.update.dao.coin.account.ksm.*;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotNominateExtrinsic;
import com.atoken.update.entity.coin.account.ksm.KsmDelegateDetail;
import com.atoken.update.entity.coin.account.ksm.KsmNominateExtrinsic;
import com.atoken.update.entity.coin.account.ksm.KsmValidator;
import com.atoken.update.entity.dot.DotValidator;
import com.atoken.update.service.coin.account.ksm.KsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class KsmServiceImpl implements KsmService {

    @Autowired
    private KsmAddressDao ksmAddressDao;

    @Autowired
    private KsmTxDao ksmTxDao;

    @Autowired
    private KsmValidatorDao ksmValidatorDao;

    @Autowired
    private KsmDelegateDetailDao ksmDelegateDetailDao;

    @Autowired
    private KsmNominateExtrinsicDao ksmNominateExtrinsicDao;

    @Override
    public List<KsmValidator> selectBondedValidators(String coin) {
        return this.ksmValidatorDao.selectBondedValidators(coin);
    }

    @Override
    public Integer insertValidator(String coin, KsmValidator ksmValidator) {
        return this.ksmValidatorDao.insertValidator(coin,ksmValidator);
    }

    @Override
    public Integer updateAmountByAddress(String coin, String address, BigDecimal amount, BigDecimal delegateAmount) {
        return this.ksmAddressDao.updateAmountByAddress(coin,address,amount,delegateAmount);
    }

    @Override
    public List<KsmNominateExtrinsic> selectNominateExtrinsic(String coin) {
        return this.ksmNominateExtrinsicDao.selectNominateExtrinsic(coin);
    }

    @Override
    public Integer updateExtrinsicStatus(String coin, KsmNominateExtrinsic ksmNominateExtrinsic) {
        return this.ksmNominateExtrinsicDao.updateExtrinsicStatus(coin,ksmNominateExtrinsic);
    }

    @Override
    public List<Address> selectStakedAddresses(String coin) {
        return this.ksmAddressDao.selectStakedAddresses(coin);
    }

    @Override
    public String selectControllerAddress(String coin, String walletId) {
        return this.ksmAddressDao.selectControllerAddress(coin,walletId);
    }

    @Override
    public Integer updateAddressDelegateAmount(String coin, BigDecimal delegateAmount, String address) {
        return this.ksmAddressDao.updateAddressDelegateAmount(coin,delegateAmount,address);
    }

    @Override
    public Integer insertDelegateDetail(String coin, KsmDelegateDetail ksmDelegateDetail) {
        return this.ksmDelegateDetailDao.insertDelegateDetail(coin,ksmDelegateDetail);
    }

    @Override
    public Integer updateStakeStatus(String coin, String address, String stakeStatus) {
        return this.ksmAddressDao.updateStakeStatus(coin, address, stakeStatus);
    }

    @Override
    public Integer updateTxStatus(String coin, Long id) {
        return this.ksmTxDao.updateTxStatus(coin,id);
    }

    @Override
    public Integer deleteDelegateByStashAddr(String coin, String stashAddr) {
        return this.ksmDelegateDetailDao.deleteDelegateByStashAddr(coin,stashAddr);
    }

    @Override
    public Integer updateAddressTypeByAddress(String coin, String address) {
        return this.ksmAddressDao.updateAddressTypeByAddress(coin,address);
    }

    @Override
    public Tx selectTxByHash(String coin, String hash) {
        return this.ksmTxDao.selectTxByHash(coin,hash);
    }

    @Override
    public List<Tx> selectUnbondTxes(String coin, Integer unbondHeight) {
        return this.ksmTxDao.selectUnbondTxes(coin,unbondHeight);
    }

    @Override
    public KsmValidator selectValidatorByAddress(String coin, String address) {
        return this.ksmValidatorDao.selectValidatorByAddress(coin, address);
    }

    @Override
    public Integer updateValidatorByAddr(String coin, DotValidator dotValidator) {
        return this.ksmValidatorDao.updateValidatorByAddr(coin, dotValidator);
    }
}
