package com.atoken.update.tomo.core;

import com.atoken.blockchain.http.request.account.tomo.ContractFunctionEnum;
import com.atoken.update.atom.common.AtomTransferMessage;
import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.TransferMessage;
import com.atoken.update.entity.coin.account.staking.DelegateDetail;
import com.atoken.update.enums.StakingStatusEnum;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.service.coin.account.AccountStakingService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.tomo.common.TomoTransferMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service("tomoBlockTxCore")
public class TomoBlockTxCore extends BlockTxCore {
    private TransferMessage transferMessage;
    @Autowired
    private AccountStakingService accountStakingService;
    @Autowired
    public TomoBlockTxCore(@Qualifier("tomoBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public TransferMessage getTransferMessage() {
        if(Objects.isNull(transferMessage)) {
            synchronized (this) {
                if(Objects.isNull(transferMessage)) {
                    transferMessage = new TomoTransferMessage();
                }
            }
        }
        return transferMessage;
    }

    @Override
    public TxAfterHandler txAfterHandler() {
        return (fromAddress, toAddress, tx) -> {
            ContractFunctionEnum contractFunctionEnum = ContractFunctionEnum.valueOf(tx.getType());
            if (contractFunctionEnum == ContractFunctionEnum.VOTE) {
                // 如果是质押交易
                // 1. 插入或者更新质押细节表
                // 2. 更新地址的质押状态
                DelegateDetail delegateDetail = new DelegateDetail();
                delegateDetail.setAddr(tx.getFromAddr());
                delegateDetail.setValidatorAddr(tx.getToAddr());
                delegateDetail.setWalletId(fromAddress.getWalletId());
                delegateDetail.setDelegateAmount(tx.getAmount());
                delegateDetail.setUnbondingAmount(BigDecimal.ZERO);
                accountStakingService.insertOrUpdateDelegateDetail(blockchainConfig.getTablePrefix(), delegateDetail);
                accountStakingService.updatePledgedByAddress(blockchainConfig.getTablePrefix(), fromAddress.getAddr());
            }
        };
    }

    @Override
    public RollBackTxAfterHander rollBackTxAfterHander() {
        return null;
    }
}
