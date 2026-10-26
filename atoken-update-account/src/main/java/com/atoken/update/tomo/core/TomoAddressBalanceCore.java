package com.atoken.update.tomo.core;

import com.atoken.blockchain.http.request.account.tomo.TomoClient;
import com.atoken.blockchain.http.request.account.tomo.TomoVotedCandidates;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.core.coin.account.AddressBalanceCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.staking.DelegateDetail;
import com.atoken.update.handler.coin.account.BalanceAfterHander;
import com.atoken.update.service.coin.account.AccountStakingService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Service("tomoAddressBalanceCore")
public class TomoAddressBalanceCore extends AddressBalanceCore {
    @Autowired
    private TomoClient tomoClient;
    @Autowired
    private AccountStakingService accountStakingService;

    @Autowired
    public TomoAddressBalanceCore(@Qualifier("tomoBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public BalanceAfterHander balanceAfterHander() {
        return (address, walletId) -> {
            boolean pledged = false;
            for (int i = 1; ; i++) {
                // 从链上获取质押信息
                TomoVotedCandidates tomoVotedCandidates = tomoClient.getVotedCandidates(address, i, 20);
                if (Objects.isNull(tomoVotedCandidates) || CollectionUtils.isEmpty(tomoVotedCandidates.getItems())) {
                    break;
                }
                if (!pledged) {
                    pledged = true;
                }
                // 用户质押细节入库
                tomoVotedCandidates.getItems().forEach(item -> {
                    DelegateDetail delegateDetail = new DelegateDetail();
                    delegateDetail.setValidatorAddr(item.getCandidate());
                    delegateDetail.setAddr(address);
                    delegateDetail.setWalletId(walletId);
                    delegateDetail.setUnbondingAmount(BigDecimal.ZERO);
                    delegateDetail.setRewardsAmount(BigDecimal.ZERO);
                    delegateDetail.setDelegateAmount(DataUtil.formatAmount(item.getCapacity(), CoinEnum.tomo.decimals()));
                    accountStakingService.insertOrUpdateDelegateDetail(blockchainConfig.getTablePrefix(), delegateDetail);
                });
            }
            if (pledged) {
                // 更新用户地址质押状态
                accountStakingService.updatePledgedByAddress(blockchainConfig.getTablePrefix(), address);
            }
        };
    }
}
