package com.atoken.update.service.dot;

import com.atoken.update.dao.dot.DotDao;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotDelegateDetail;
import com.atoken.update.entity.dot.DotNominateExtrinsic;
import com.atoken.update.entity.dot.DotValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DotServiceImpl implements DotService {
    @Autowired
    private DotDao dotDao;
    @Override
    public List<DotValidator> selectBondedValidators() {
        return dotDao.selectBondedValidators();
    }

    @Override
    public DotValidator selectValidatorByAddress(String address) {
        return dotDao.selectValidatorByAddress(address);
    }

    @Override
    public Integer insertValidator(DotValidator dotValidator) {
        return dotDao.insertValidator(dotValidator);
    }

    @Override
    public Integer updateValidatorByAddr(DotValidator dotValidator) {
        return dotDao.updateValidatorByAddr(dotValidator);
    }

    @Override
    public List<Tx> selectUnbondTxes(Integer unbondHeight) {
        return dotDao.selectUnbondTxes(unbondHeight);
    }

    @Override
    public Integer insertDelegateDetail(DotDelegateDetail dotDelegateDetail) {
        return dotDao.insertDelegateDetail(dotDelegateDetail);
    }

    @Override
    public Integer deleteDelegateByStashAddr(String stashAddr) {
        return dotDao.deleteDelegateByStashAddr(stashAddr);
    }

    @Override
    public Integer updateAddressDelegateAmount(BigDecimal delegateAmount, String address) {
        return dotDao.updateAddressDelegateAmount(delegateAmount, address);
    }

    @Override
    public Integer updateAmountByAddress(String address, BigDecimal amount, BigDecimal delegateAmount) {
        return dotDao.updateAmountByAddress(address, amount, delegateAmount);
    }

    @Override
    public Integer updateTxStatus(Long id) {
        return dotDao.updateTxStatus(id);
    }

    @Override
    public Integer updateStakeStatus(String address, String stakeStatus) {
        return dotDao.updateStakeStatus(address, stakeStatus);
    }

    @Override
    public List<DotNominateExtrinsic> selectNominateExtrinsic() {
        return dotDao.selectNominateExtrinsic();
    }

    @Override
    public Integer updateExtrinsicStatus(DotNominateExtrinsic dotNominateExtrinsic) {
        return dotDao.updateExtrinsicStatus(dotNominateExtrinsic);
    }

    @Override
    public Tx selectTxByHash(String hash) {
        return dotDao.selectTxByHash(hash);
    }

    @Override
    public List<Address> selectStakedAddresses() {
        return dotDao.selectStakedAddresses();
    }

    @Override
    public String selectControllerAddress(String walletId) {
        return dotDao.selectControllerAddress(walletId);
    }

    @Override
    public Integer updateAddressTypeByAddress(String address) {
        return dotDao.updateAddressTypeByAddress(address);
    }
}
