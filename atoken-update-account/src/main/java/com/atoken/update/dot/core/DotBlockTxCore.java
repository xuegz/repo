package com.atoken.update.dot.core;

import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.BalanceInfoResp;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.core.BlockTxCore;
import com.atoken.update.dot.enums.StakeStatus;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.TransferMessage;
import com.atoken.update.entity.dot.DotDelegateDetail;
import com.atoken.update.handler.RollBackTxAfterHander;
import com.atoken.update.handler.TxAfterHandler;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.TxService;
import com.atoken.update.service.dot.DotService;
import org.apache.commons.lang.StringUtils;
import org.polkadot.types.type.Exposure;
import org.polkadot.types.type.IndividualExposure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

// TODO dot质押逻辑，二期
@Service("dotBlockTxCore")
public class DotBlockTxCore extends BlockTxCore {
    @Autowired
    private DotSidecarClient dotSidecarClient;
    @Autowired
    private DotClient dotClient;
    @Autowired
    private DotService dotService;
    private TransferMessage transferMessage = new TransferMessage();

    @Autowired
    public DotBlockTxCore(@Qualifier("dotStakingBlockHandlerService") BlockHandlerService blockHandlerService,
                          @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig,
                          @Qualifier("dotTxService") TxService txService) {
        super(blockHandlerService, blockchainConfig, txService);
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
                    BalanceInfoResp toResp = this.dotSidecarClient.balanceInfo(toAddress.getAddr());
                    BigDecimal toAmount = DataUtil.formatAmountByDecimal(toResp.getFree().subtract(toResp.getMiscFrozen()), CoinEnum.dot.decimals());
                    dotService.updateAmountByAddress(toAddress.getAddr(), toAmount,  BigDecimal.ZERO);
                }

                if (fromAddress != null &&  StringUtils.isNotEmpty(fromAddress.getAddr())) {
                    BalanceInfoResp fromResp = this.dotSidecarClient.balanceInfo(fromAddress.getAddr());
                    BigDecimal fromAmount = DataUtil.formatAmountByDecimal(fromResp.getFree().subtract(fromResp.getMiscFrozen()), CoinEnum.dot.decimals());
                    dotService.updateAmountByAddress(fromAddress.getAddr(), fromAmount,  BigDecimal.ZERO);
                }
            } catch (Exception  e) {
                logger.error("更新余额失败。", e.getMessage());
            }
        };
    }

    private <L extends List> void saveDelegateDetail(Address fromAddress, String stashAddress, L validators) {
        dotService.deleteDelegateByStashAddr(stashAddress);
        validators.forEach(validator -> {
            Exposure exposure = dotClient.getStakersByValidatorAddr(validator.toString());
            try {
                BigDecimal delegateAmount = BigDecimal.ZERO;
                try {
                    IndividualExposure individualExposure = exposure.getOthers().stream().filter(delegator -> stashAddress.equals(delegator.getWho().toString())).findFirst().get();
                    if (Objects.nonNull(individualExposure)) {
                        delegateAmount = DataUtil.formatAmount(new BigDecimal(individualExposure.getValue().toBn()), CoinEnum.dot.decimals());
                    }
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage());
                }

                DotDelegateDetail dotDelegateDetail = new DotDelegateDetail();
                dotDelegateDetail.setAddr(stashAddress);
                dotDelegateDetail.setDelegateAmount(delegateAmount);
                dotDelegateDetail.setValidatorAddr(validator.toString());
                dotDelegateDetail.setWalletId(fromAddress.getWalletId());
                dotService.insertDelegateDetail(dotDelegateDetail);
            } catch (Exception e) {
                logger.error(e.toString());
            }
        });
        dotService.updateStakeStatus(stashAddress, StakeStatus.STAKED.name());
    }

    @Override
    public RollBackTxAfterHander rollBackTxAfterHander() {
        return null;
    }
}
