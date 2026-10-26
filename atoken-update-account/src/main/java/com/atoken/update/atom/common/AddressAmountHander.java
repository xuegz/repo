package com.atoken.update.atom.common;

import com.atoken.blockchain.http.request.account.atom.*;
import com.atoken.blockchain.http.request.enums.AtomCoin;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.blockchain.utils.VerifyUtil;
import com.atoken.update.entity.Final;
import com.atoken.update.entity.atom.AtomAmountInfo;
import com.atoken.update.service.atom.AtomService;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AddressAmountHander {
    public static void addressAmountHandler(String address, AtomClient atomClient, AtomService atomService) {
        AtomAmountInfo atomAmountInfo = getAddressAmountInfo(address, atomClient);
        // 更新资产信息
        Integer row = atomService.updateAmountByAddress(address, atomAmountInfo.getDelegationAmount(), atomAmountInfo.getUnBondingAmount(), atomAmountInfo.getRewardsAmount());
        VerifyUtil.numberNotZore(row, "更新资产失败");
    }

    public static AtomAmountInfo getDelegateDetailAmount(String delegatorAddress, String validatorAddress, AtomClient atomClient) {
        AtomDelegation atomDelegation = atomClient.getDelegationByValidator(delegatorAddress, validatorAddress);
        AtomUnbondingDelegation atomUnbondingDelegation= atomClient.getUnbondingDelegationsByValidator(delegatorAddress, validatorAddress);
        BigDecimal delegateAmount = Objects.nonNull(atomDelegation) ? DataUtil.formatAmount(atomDelegation.getShares(), CoinEnum.atom.decimals()): BigDecimal.ZERO;
        Final<BigDecimal> unBondingAmount = Final.newFinal(BigDecimal.ZERO);
        if(Objects.nonNull(atomUnbondingDelegation)) {
            atomUnbondingDelegation.getEntries().forEach(unbonding -> {
                unBondingAmount.setType(unBondingAmount.getType().add(unbonding.getBalance()));
            });
            unBondingAmount.setType(DataUtil.formatAmount(unBondingAmount.getType(), CoinEnum.atom.decimals()));
        }
        AtomAmountInfo atomAmountInfo = new AtomAmountInfo();
        atomAmountInfo.setDelegationAmount(delegateAmount);
        atomAmountInfo.setUnBondingAmount(unBondingAmount.getType());
        return atomAmountInfo;
    }

    public static AtomAmountInfo getAddressAmountInfo(String address, AtomClient atomClient) {
        // 获取用户质押信息
        List<AtomDelegationResult> atomDelegations = atomClient.getDelegations(address);
        // 获取用户解绑信息
        List<AtomUnbondingDelegation> atomUnbondingDelegations = atomClient.getUnbondingDelegations(address);
        // 获取用户收益信息
        List<AtomAmount> rewardsAmounts = atomClient.getDelegatorRewards(address);

        // 计算质押金额
        Final<BigDecimal> delegationAmount = Final.newFinal(BigDecimal.ZERO);
        if(!CollectionUtils.isEmpty(atomDelegations)) {
            atomDelegations.forEach(atomDelegation -> delegationAmount.setType(delegationAmount.getType().add(atomDelegation.getDelegation().getShares())));
        }

        // 计算待解绑信息
        Final<BigDecimal> unBondingAmount = Final.newFinal(BigDecimal.ZERO);
        if(!CollectionUtils.isEmpty(atomUnbondingDelegations)) {
            atomUnbondingDelegations.forEach(atomUnbondingDelegation -> {

                Final<BigDecimal> tmpAmount = Final.newFinal(BigDecimal.ZERO);
                if(!CollectionUtils.isEmpty(atomUnbondingDelegation.getEntries())) {
                    atomUnbondingDelegation.getEntries().forEach(atomEntrie -> tmpAmount.setType(tmpAmount.getType().add(atomEntrie.getBalance())));
                }
                unBondingAmount.setType(unBondingAmount.getType().add(tmpAmount.getType()));
            });
        }

        // 计算总收益信息
        Final<BigDecimal> rewardsAmount = Final.newFinal(BigDecimal.ZERO);
        if(!CollectionUtils.isEmpty(rewardsAmounts)) {
            rewardsAmounts.stream().filter(reward -> AtomCoin.uatom.name().equals(reward.getDenom())).forEach(reward -> rewardsAmount.setType(rewardsAmount.getType().add(reward.getAmount())));
        }

        delegationAmount.setType(DataUtil.formatAmount(delegationAmount.getType(), CoinEnum.atom.decimals()));
        unBondingAmount.setType(DataUtil.formatAmount(unBondingAmount.getType(), CoinEnum.atom.decimals()));
        rewardsAmount.setType(DataUtil.formatAmount(rewardsAmount.getType(), CoinEnum.atom.decimals()));

        AtomAmountInfo atomAmountInfo = new AtomAmountInfo();
        atomAmountInfo.setDelegationAmount(delegationAmount.getType());
        atomAmountInfo.setRewardsAmount(rewardsAmount.getType());
        atomAmountInfo.setUnBondingAmount(unBondingAmount.getType());
        return atomAmountInfo;
    }
}
