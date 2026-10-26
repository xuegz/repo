package com.atoken.update.kusama.core;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.dot.cache.DotUnbondingTimeCache;
import com.atoken.update.dot.core.DotUnbondedExpireCore;
import com.atoken.update.dot.enums.StakeStatus;
import com.atoken.update.entity.Tx;
import com.atoken.update.kusama.cache.KsmUnbondingTimeCache;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.kusama.service.KsmSidecarBlockHandlerService;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.coin.account.ksm.KsmService;
import com.atoken.update.util.AtokenLogger;
import org.polkadot.types.type.StakingLedger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class KsmUnbondedExpireCore implements AtokenLogger {

    @Autowired
    private KsmService ksmService;

    @Autowired
    private KsmClient ksmClient;

    @Autowired
    private KsmUnbondingTimeCache ksmUnbondingTimeCache;

    @Autowired
    private KsmSidecarBlockHandlerService blockHandlerService;

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    public void unbondedExpireHandler() {
        logger.info("KSM解绑质押更新程序开始");
        // 从链上获取解绑延迟高度
        Integer heightNum = ksmUnbondingTimeCache.get(DotUnbondingTimeCache.CACHE_KEY);
        // 获取链上当前高度
        Integer currentHeight = blockHandlerService.getBlockNumber();
        // 获取数据库中
        List<Tx> txList = ksmService.selectUnbondTxes(ksmBlockchainConfig.getTablePrefix(),currentHeight - heightNum);
        txList.forEach(tx -> {
            // 获取质押信息
            StakingLedger stakingLedger = ksmClient.getLedger(tx.getFromAddr());
            // 以前质押分两个地址
            String stashAddress = stakingLedger.getStash().toString();
            // 通过地址删除质押信息
            ksmService.deleteDelegateByStashAddr(ksmBlockchainConfig.getTablePrefix(),stashAddress);
            BigDecimal delegateAmount = DataUtil.formatAmount(new BigDecimal(stakingLedger.getActive().toBn()), CoinEnum.ksm.decimals());
            // 获取账户余额
            BigDecimal balance = blockHandlerService.getAddressBalance(stashAddress);
            // 更新相应数据库记录
            this.ksmService.updateAmountByAddress(ksmBlockchainConfig.getTablePrefix(),stashAddress, balance, delegateAmount);
            ksmService.updateStakeStatus(ksmBlockchainConfig.getTablePrefix(),stashAddress, StakeStatus.NO_STAKE.name());
            ksmService.updateTxStatus(ksmBlockchainConfig.getTablePrefix(),tx.getId());
        });
        logger.info("KSM解绑质押更新程序结束");
    }
}
