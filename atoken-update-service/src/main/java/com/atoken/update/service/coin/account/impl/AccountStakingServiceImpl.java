package com.atoken.update.service.coin.account.impl;

import com.atoken.update.dao.coin.account.staking.StakingDao;
import com.atoken.update.entity.coin.account.AmountInfo;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.entity.coin.account.staking.DelegateDetail;
import com.atoken.update.entity.coin.account.staking.ValidatorEO;
import com.atoken.update.service.coin.account.AccountStakingService;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountStakingServiceImpl implements AccountStakingService {
    @Autowired
    private StakingDao stakingDao;

    @Override
    public Integer updateAmountByAddress(String coin, String address, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount) {
        return stakingDao.updateAmountByAddress(coin, address, delegateAmount, unbondingAmount, rewardsAmount);
    }

    @Override
    public Integer updatePledgedByAddress(String coin, String address) {
        return stakingDao.updatePledgedByAddress(coin, address);
    }

    @Override
    public List<ValidatorEO> selectBondedValidators(String coin) {
        return stakingDao.selectBondedValidators(coin);
    }

    @Override
    public ValidatorEO selectValidatorByAddress(String coin, String address) {
        return stakingDao.selectValidatorByAddress(coin, address);
    }

    @Override
    public Integer insertValidator(String coin, ValidatorEO validator) {
        return stakingDao.insertValidator(coin, validator);
    }

    @Override
    public Integer updateValidatorByAddress(String coin, ValidatorEO validator) {
        return stakingDao.updateValidatorByAddress(coin, validator);
    }

    @Override
    public Integer insertUpdateValidator(String coin, ValidatorEO validator) {
        return stakingDao.insertUpdateValidator(coin, validator);
    }

    @Override
    public List<Tx> selectUnbondedTxs(String coin, Long overTime) {
        return stakingDao.selectUnbondedTxs(coin, overTime);
    }

    @Override
    public Integer updateAmountByAddress(String coin, String address, BigDecimal amount, BigDecimal delegateAmount, BigDecimal unbondingAmount, BigDecimal rewardsAmount) {
        return stakingDao.updateAmountByAddress(coin, address, delegateAmount, unbondingAmount, rewardsAmount);
    }

    @Override
    public Integer updateTxStatus(String coin, Long id) {
        return stakingDao.updateTxStatus(coin, id);
    }

    @Override
    public void unbondingExpire(String coin, Long id, String address, String validatorAddress, BigDecimal amount, AmountInfo amountInfo, DelegateDetail delegateDetail) {
        Integer row = stakingDao.updateAllAmountByAddress(coin, address, amount, amountInfo.getDelegationAmount(), amountInfo.getUnBondingAmount(), amountInfo.getRewardsAmount());
        VerifyUtil.numberNotZore(row, address + "更新余额成功");

        row = stakingDao.updateTxStatus(coin, id);
        VerifyUtil.numberNotZore(row, address + "更新交易状态成功");

        row = stakingDao.updateDelegateDetail(coin, delegateDetail);
        VerifyUtil.numberNotZore(row, address + "更新质押细节表失败");
    }

    @Override
    public Integer insertOrUpdateDelegateDetail(String coin, DelegateDetail delegateDetail) {
        return stakingDao.insertOrUpdateDelegateDetail(coin, delegateDetail);
    }

    @Override
    public Integer updateStakingStatus(String coin, DelegateDetail delegateDetail) {
        return stakingDao.updateStakingStatus(coin, delegateDetail);
    }

    @Override
    public Integer deleteDelegate(String coin, String addr, String validatorAddr) {
        return stakingDao.deleteDelegate(coin, addr, validatorAddr);
    }

    @Override
    @Transactional
    public void unbondedExpireHandler(String coin, Tx tx, BigDecimal delegateAmount) {
        if (!BigDecimal.ZERO.equals(delegateAmount)) {
            // 如果质押不为0，更新相关质押金额
            DelegateDetail delegateDetail = new DelegateDetail();
            delegateDetail.setRewardsAmount(BigDecimal.ZERO);
            delegateDetail.setUnbondingAmount(BigDecimal.ZERO);
            delegateDetail.setDelegateAmount(delegateAmount);
            delegateDetail.setAddr(tx.getFromAddr());
            delegateDetail.setValidatorAddr(tx.getToAddr());
            stakingDao.updateDelegateDetail(coin, delegateDetail);
        } else {
            // 如果金额为0，删除质押信息
            stakingDao.deleteDelegate(coin, tx.getFromAddr(), tx.getToAddr());
        }
        // 更新交易状态
        stakingDao.updateTxStatus(coin, tx.getId());
    }

    @Override
    public Integer updateDelegateDetail(String coin, DelegateDetail delegateDetail) {
        return stakingDao.updateDelegateDetail(coin, delegateDetail);
    }
}
