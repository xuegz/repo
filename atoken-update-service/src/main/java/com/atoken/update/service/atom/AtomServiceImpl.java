package com.atoken.update.service.atom;

import com.atoken.update.dao.atom.AtomDao;
import com.atoken.update.entity.atom.AtomAmountInfo;
import com.atoken.update.entity.atom.AtomDelegateDetail;
import com.atoken.update.entity.atom.AtomTx;
import com.atoken.update.entity.atom.AtomValidatorEO;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AtomServiceImpl implements AtomService {
    @Autowired
    private AtomDao atomDao;

    @Override
    public Integer updateAmountByAddress(String address, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount) {
        return atomDao.updateAmountByAddress(address, delegateAmount, unbondingAmount, rewardsAmount);
    }

    @Override
    public String selectAccountNumByAddress(String address) {
        return atomDao.selectAccountNumByAddress(address);
    }

    @Override
    public Integer updateTxAccountNum(Long id, String accountNum) {
        return atomDao.updateTxAccountNum(id, accountNum);
    }

    @Override
    public List<AtomValidatorEO> selectBondedValidators() {
        return atomDao.selectBondedValidators();
    }

    @Override
    public AtomValidatorEO selectValidatorByAddress(String address) {
        return atomDao.selectValidatorByAddress(address);
    }

    @Override
    public Integer insertValidator(AtomValidatorEO atomValidator) {
        return atomDao.insertValidator(atomValidator);
    }

    @Override
    public Integer updateValidatorByAddress(AtomValidatorEO atomValidator) {
        return atomDao.updateValidatorByAddress(atomValidator);
    }

    @Override
    public Integer insertUpdateValidator(AtomValidatorEO atomValidator) {
        return atomDao.insertUpdateValidator(atomValidator);
    }

    @Override
    public List<AtomTx> selectUnbondedAtomTxs(Long currentTime, Long unbondingTime) {
        return atomDao.selectUnbondedAtomTxs(currentTime, unbondingTime);
    }

    @Override
    public Integer updateAmountByAddress(String address, BigDecimal amount, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount) {
        return atomDao.updateAllAmountByAddress(address, amount,delegateAmount, unbondingAmount, rewardsAmount);
    }

    @Override
    public Integer updateTxStatus(Long id) {
        return atomDao.updateTxStatus(id);
    }

    @Override
    @Transactional
    public void unbondingExpire(Long id, String address, String validatorAddress, BigDecimal amount, AtomAmountInfo atomAmountInfo, AtomDelegateDetail atomDelegateDetail) {
        Integer row = atomDao.updateAllAmountByAddress(address, amount, atomAmountInfo.getDelegationAmount(), atomAmountInfo.getUnBondingAmount(), atomAmountInfo.getRewardsAmount());
        VerifyUtil.numberNotZore(row, address + "更新余额成功");

        row = atomDao.updateTxStatus(id);
        VerifyUtil.numberNotZore(row, address + "更新交易状态成功");

        row = atomDao.updateDelegateDetail(atomDelegateDetail);
        VerifyUtil.numberNotZore(row, address + "更新质押细节表失败");
    }

    @Override
    public Integer insertOrUpdateDelegateDetail(AtomDelegateDetail atomDelegateDetail) {
        return atomDao.insertOrUpdateDelegateDetail(atomDelegateDetail);
    }

    @Override
    public Integer updateDelegateDetail(AtomDelegateDetail atomDelegateDetail) {
        return atomDao.updateDelegateDetail(atomDelegateDetail);
    }

    @Override
    public String selectWalletIdByAddress(String address) {
        return atomDao.selectWalletIdByAddress(address);
    }
}
