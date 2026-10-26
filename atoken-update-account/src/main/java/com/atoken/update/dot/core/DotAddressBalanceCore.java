package com.atoken.update.dot.core;

import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.core.AddressBalanceCore;
import com.atoken.update.dot.enums.StakeStatus;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.dot.DotDelegateDetail;
import com.atoken.update.handler.BalanceAfterHander;
import com.atoken.update.service.AddressService;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.dot.DotService;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.Exposure;
import org.polkadot.types.type.IndividualExposure;
import org.polkadot.types.type.StakingLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Objects;

// TODO dot质押逻辑， 二期
@Service("dotAddressBalanceCore")
public class DotAddressBalanceCore extends AddressBalanceCore {

    @Autowired
    private DotService dotService;
    @Autowired
    private DotClient dotClient;
    @Autowired
    private AddressService addressService;

    @Autowired
    public DotAddressBalanceCore(@Qualifier("dotSidecarBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }


    @Override
    public BalanceAfterHander balanceAfterHander() {
        return (address, walletId) -> {
            try {
                // 从链上获取控制者账户
                AccountId controller = dotClient.getControllerByStashAddr(address);
                if (Objects.nonNull(controller)) {
                    // 通过地址更新账户类型
                    dotService.updateAddressTypeByAddress(controller.toString());
                    // 获取用户质押信息
                    StakingLedger stakingLedger = dotClient.getLedger(controller.toString());

                    // 更新质押状态
                    if (!CollectionUtils.isEmpty(stakingLedger.getUnlocking())) {
                        dotService.updateStakeStatus(address, StakeStatus.CANCEL_STAKE.name());
                    }

                    // 更新质押金额
                    BigDecimal delegateAmount = DataUtil.formatAmount(new BigDecimal(stakingLedger.getActive().toBn()), CoinEnum.dot.decimals());
                    dotService.updateAddressDelegateAmount(delegateAmount, address);
                    // 通过地址获取质押人地址
                    Vector<AccountId> validators = dotClient.getValidatorsByDelegatorAddr(address);
                    // 更新质押状态
                    if (CollectionUtils.isEmpty(validators)) {
                        dotService.updateStakeStatus(address, StakeStatus.STAKING.name());
                    } else {
                        dotService.updateStakeStatus(address, StakeStatus.STAKED.name());
                    }
                    // 删除质押信息
                    dotService.deleteDelegateByStashAddr(address);
                    validators.forEach(validator -> {
                        // 获取验证人信息
                        Exposure exposure = dotClient.getStakersByValidatorAddr(validator.toString());

                        // 入库质押信息表
                        try {
                            BigDecimal amount = BigDecimal.ZERO;
                            try {
                                IndividualExposure individualExposure = exposure.getOthers().stream().filter(delegator -> address.equals(delegator.getWho().toString())).findFirst().get();
                                if (Objects.nonNull(individualExposure)) {
                                    amount = DataUtil.formatAmount(new BigDecimal(individualExposure.getValue().toBn()), CoinEnum.dot.decimals());
                                }
                            } catch (Exception e) {
                                logger.error(e.getLocalizedMessage());
                            }
                            DotDelegateDetail dotDelegateDetail = new DotDelegateDetail();
                            dotDelegateDetail.setAddr(address);
                            dotDelegateDetail.setDelegateAmount(amount);
                            dotDelegateDetail.setValidatorAddr(validator.toString());
                            dotDelegateDetail.setWalletId(walletId);
                            dotService.insertDelegateDetail(dotDelegateDetail);
                        } catch (Exception e) {
                            logger.error(e.toString());
                        }
                    });
                }
            } catch (Exception e) {
                logger.error("DotAddressBalanceCore balanceAfterHander", e);
            }
        };
    }
}
