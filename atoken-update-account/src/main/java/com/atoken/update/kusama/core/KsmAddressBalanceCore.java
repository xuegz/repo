package com.atoken.update.kusama.core;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.dot.enums.StakeStatus;
import com.atoken.update.entity.coin.account.ksm.KsmDelegateDetail;
import com.atoken.update.handler.coin.account.BalanceAfterHander;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.service.AddressService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.service.coin.account.ksm.KsmService;
import org.apache.commons.collections4.CollectionUtils;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.Exposure;
import org.polkadot.types.type.IndividualExposure;
import org.polkadot.types.type.StakingLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service("ksmAddressBalanceCore")
public class KsmAddressBalanceCore extends AddressBalanceCore {

    @Autowired
    private KsmClient ksmClient;

    @Autowired
    private KsmService ksmService;

    @Autowired
    private AddressService addressService;

    @Autowired
    public KsmAddressBalanceCore(@Qualifier("ksmSidecarBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("ksmBlockchainConfig") KsmBlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }


    @Override
    public BalanceAfterHander balanceAfterHander() {
        return (address, walletId) -> {
            try {
                // 从链上获取控制者账户
                AccountId controller = this.ksmClient.getControllerByStashAddr(address);
                if (Objects.nonNull(controller)) {
                    // 通过地址更新账户类型
                    this.ksmService.updateAddressTypeByAddress(blockchainConfig.getTablePrefix(),controller.toString());
                    // 获取用户质押信息
                    StakingLedger stakingLedger = this.ksmClient.getLedger(controller.toString());

                    // 更新质押状态
                    if (!CollectionUtils.isEmpty(stakingLedger.getUnlocking())) {
                        this.ksmService.updateStakeStatus(blockchainConfig.getTablePrefix(),address, StakeStatus.CANCEL_STAKE.name());
                    }

                    // 更新质押金额
                    BigDecimal delegateAmount = DataUtil.formatAmount(new BigDecimal(stakingLedger.getActive().toBn()), CoinEnum.ksm.decimals());
                    this.ksmService.updateAddressDelegateAmount(blockchainConfig.getTablePrefix(),delegateAmount, address);
                    // 通过地址获取质押人地址
                    Vector<AccountId> validators = this.ksmClient.getValidatorsByDelegatorAddr(address);
                    // 更新质押状态
                    if (CollectionUtils.isEmpty(validators)) {
                        this.ksmService.updateStakeStatus(blockchainConfig.getTablePrefix(),address, StakeStatus.STAKING.name());
                    } else {
                        this.ksmService.updateStakeStatus(blockchainConfig.getTablePrefix(),address, StakeStatus.STAKED.name());
                    }
                    // 删除质押信息
                    this.ksmService.deleteDelegateByStashAddr(blockchainConfig.getTablePrefix(),address);
                    validators.forEach(validator -> {
                        // 获取验证人信息
                        Exposure exposure = this.ksmClient.getStakersByValidatorAddr(validator.toString());

                        // 入库质押信息表
                        try {
                            BigDecimal amount = BigDecimal.ZERO;
                            try {
                                IndividualExposure individualExposure = exposure.getOthers().stream().filter(delegator -> address.equals(delegator.getWho().toString())).findFirst().get();
                                if (Objects.nonNull(individualExposure)) {
                                    amount = DataUtil.formatAmount(new BigDecimal(individualExposure.getValue().toBn()), CoinEnum.ksm.decimals());
                                }
                            } catch (Exception e) {
                                logger.error(e.getLocalizedMessage());
                            }
                            KsmDelegateDetail dotDelegateDetail = new KsmDelegateDetail();
                            dotDelegateDetail.setAddr(address);
                            dotDelegateDetail.setDelegateAmount(amount);
                            dotDelegateDetail.setValidatorAddr(validator.toString());
                            dotDelegateDetail.setWalletId(walletId);
                            this.ksmService.insertDelegateDetail(blockchainConfig.getTablePrefix(),dotDelegateDetail);
                        } catch (Exception e) {
                            logger.error(e.toString());
                        }
                    });
                }
            } catch (Exception e) {
                logger.error("KsmAddressBalanceCore balanceAfterHander", e);
            }
        };
    }
}
