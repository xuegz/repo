package com.atoken.update.kusama.core;

import com.atoken.blockchain.http.request.account.dot.sidecar.dto.BalanceInfoResp;
import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.blockchain.http.request.account.ksm.sidecar.KsmSidecarClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.dot.enums.StakeStatus;
import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.TransferMessage;
import com.atoken.update.entity.coin.account.ksm.KsmDelegateDetail;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.service.coin.account.ksm.KsmService;
import org.apache.commons.lang.StringUtils;
import org.polkadot.types.type.Exposure;
import org.polkadot.types.type.IndividualExposure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service("ksmBlockTxCore")
public class KsmBlockTxCore extends BlockTxCore {

    @Autowired
    private KsmClient ksmClient;

    @Autowired
    private KsmSidecarClient ksmSidecarClient;

    @Autowired
    private KsmService ksmService;

    private TransferMessage transferMessage = new TransferMessage();

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    @Autowired
    public KsmBlockTxCore(@Qualifier("ksmStakingBlockHandlerService") BlockHandlerService blockHandlerService,
                          @Qualifier("ksmBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public TransferMessage getTransferMessage() {
        return transferMessage;
    }

    @Override
    public TxAfterHandler txAfterHandler() {
        return (fromAddress, toAddress, tx) -> {
            try {
                if (toAddress != null &&  StringUtils.isNotEmpty(toAddress.getAddr())) {
                    BalanceInfoResp toResp = this.ksmSidecarClient.balanceInfo(toAddress.getAddr());
                    BigDecimal toAmount = DataUtil.formatAmountByDecimal(toResp.getFree().subtract(toResp.getMiscFrozen()), CoinEnum.ksm.decimals());
                    ksmService.updateAmountByAddress(ksmBlockchainConfig.getTablePrefix(), toAddress.getAddr(), toAmount,  BigDecimal.ZERO);
                }

                if (fromAddress != null &&  StringUtils.isNotEmpty(fromAddress.getAddr())) {
                    BalanceInfoResp fromResp = this.ksmSidecarClient.balanceInfo(fromAddress.getAddr());
                    BigDecimal fromAmount = DataUtil.formatAmountByDecimal(fromResp.getFree().subtract(fromResp.getMiscFrozen()), CoinEnum.ksm.decimals());
                    ksmService.updateAmountByAddress(ksmBlockchainConfig.getTablePrefix(), fromAddress.getAddr(), fromAmount,  BigDecimal.ZERO);
                }

            } catch (Exception  e) {
                logger.error("更新余额失败。", e.getMessage());
            }
        };
    }

    private <L extends List> void saveDelegateDetail(Address fromAddress, String stashAddress, L validators) {
        this.ksmService.deleteDelegateByStashAddr(ksmBlockchainConfig.getTablePrefix(),stashAddress);
        validators.forEach(validator -> {
            Exposure exposure = this.ksmClient.getStakersByValidatorAddr(validator.toString());
            try {
                BigDecimal delegateAmount = BigDecimal.ZERO;
                try {
                    IndividualExposure individualExposure = exposure.getOthers().stream().filter(delegator -> stashAddress.equals(delegator.getWho().toString())).findFirst().get();
                    if (Objects.nonNull(individualExposure)) {
                        delegateAmount = DataUtil.formatAmount(new BigDecimal(individualExposure.getValue().toBn()), CoinEnum.ksm.decimals());
                    }
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage());
                }

                KsmDelegateDetail dotDelegateDetail = new KsmDelegateDetail();
                dotDelegateDetail.setAddr(stashAddress);
                dotDelegateDetail.setDelegateAmount(delegateAmount);
                dotDelegateDetail.setValidatorAddr(validator.toString());
                dotDelegateDetail.setWalletId(fromAddress.getWalletId());
                this.ksmService.insertDelegateDetail(ksmBlockchainConfig.getTablePrefix(),dotDelegateDetail);
            } catch (Exception e) {
                logger.error(e.toString());
            }
        });
        this.ksmService.updateStakeStatus(ksmBlockchainConfig.getTablePrefix(),stashAddress, StakeStatus.STAKED.name());
    }

    @Override
    public RollBackTxAfterHander rollBackTxAfterHander() {
        return null;
    }
}
