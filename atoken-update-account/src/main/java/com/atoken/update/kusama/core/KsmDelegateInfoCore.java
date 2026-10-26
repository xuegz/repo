package com.atoken.update.kusama.core;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.dot.core.DotDelegateInfoCore;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.coin.account.ksm.KsmDelegateDetail;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.kusama.service.KsmSidecarBlockHandlerService;
import com.atoken.update.service.coin.account.ksm.KsmService;
import com.atoken.update.util.AtokenLogger;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.Exposure;
import org.polkadot.types.type.IndividualExposure;
import org.polkadot.types.type.StakingLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class KsmDelegateInfoCore implements AtokenLogger {

    @Autowired
    private KsmService ksmService;

    @Autowired
    private KsmClient ksmClient;

    @Autowired
    private KsmSidecarBlockHandlerService ksmSidecarBlockHandlerService;

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    public void delegateInfoHandler() {
        // 获取有质押地址的列表
        List<Address> addressList = this.ksmService.selectStakedAddresses(ksmBlockchainConfig.getTablePrefix());
        addressList.forEach(address -> {
            try {
                // 获取该钱包的控制地址
                String controllerAddress = this.ksmService.selectControllerAddress(ksmBlockchainConfig.getTablePrefix(),address.getWalletId());
                // 获取该地址的余额
                BigDecimal balance = ksmSidecarBlockHandlerService.getAddressBalance(address.getAddr());
                // 获取质押信息
                StakingLedger stakingLedger = this.ksmClient.getLedger(controllerAddress);
                // 计算质押金额
                BigDecimal delegateAmount = DataUtil.formatAmount(new BigDecimal(stakingLedger.getActive().toBn()), CoinEnum.ksm.decimals());
                // 从链上获取质押地址
                Vector<AccountId> validators = this.ksmClient.getValidatorsByDelegatorAddr(address.getAddr());
                // 更新余额
                this.ksmService.updateAmountByAddress(ksmBlockchainConfig.getTablePrefix(),address.getAddr(), balance, delegateAmount);
                // 更新质押信息
                this.ksmService.deleteDelegateByStashAddr(ksmBlockchainConfig.getTablePrefix(),address.getAddr());
                validators.forEach(validator -> {
                    Exposure exposure = this.ksmClient.getStakersByValidatorAddr(validator.toString());
                    try {
                        BigDecimal amount = BigDecimal.ZERO;
                        try {
                            IndividualExposure individualExposure = exposure.getOthers().stream().filter(delegator -> address.getAddr().equals(delegator.getWho().toString())).findFirst().get();
                            if (Objects.nonNull(individualExposure)) {
                                amount = DataUtil.formatAmount(new BigDecimal(individualExposure.getValue().toBn()), CoinEnum.ksm.decimals());
                            }
                        } catch (Exception e) {
                            logger.error(e.getLocalizedMessage());
                        }
                        KsmDelegateDetail dotDelegateDetail = new KsmDelegateDetail();
                        dotDelegateDetail.setAddr(address.getAddr());
                        dotDelegateDetail.setDelegateAmount(amount);
                        dotDelegateDetail.setValidatorAddr(validator.toString());
                        dotDelegateDetail.setWalletId(address.getWalletId());
                        this.ksmService.insertDelegateDetail(this.ksmBlockchainConfig.getTablePrefix(),dotDelegateDetail);
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                });
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        });
    }
}
