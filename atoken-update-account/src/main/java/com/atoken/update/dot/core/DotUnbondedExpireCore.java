package com.atoken.update.dot.core;

import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.Ledger;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.dot.cache.DotUnbondingTimeCache;
import com.atoken.update.dot.enums.StakeStatus;
import com.atoken.update.entity.Tx;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.dot.DotService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DotUnbondedExpireCore implements AtokenLogger {
    @Autowired
    private DotService dotService;

    @Autowired
    private DotUnbondingTimeCache dotUnbondingTimeCache;

    @Autowired
    @Qualifier("dotSidecarBlockHandlerService")
    private BlockHandlerService blockHandlerService;

    @Autowired
    private DotSidecarClient dotSidecarClient;

    public void unbondedExpireHandler() {
        logger.info("DOT解绑质押更新程序开始");
        // 从链上获取解绑延迟高度
        Integer heightNum = dotUnbondingTimeCache.get(DotUnbondingTimeCache.CACHE_KEY);
        // 获取链上当前高度
        Integer currentHeight = blockHandlerService.getBlockNumber();
        // 获取数据库中
        List<Tx> txList = dotService.selectUnbondTxes(currentHeight - heightNum);
        txList.forEach(tx -> {
            // 获取质押信息
            Ledger stakingLedger = dotSidecarClient.stakingLedger(tx.getFromAddr());
            // 以前质押分两个地址
            String stashAddress = stakingLedger.getStash();

            BigDecimal delegateAmount = DataUtil.formatAmount(new BigDecimal(stakingLedger.getActive()), CoinEnum.dot.decimals());
            // 获取账户余额
            BigDecimal balance = blockHandlerService.getAddressBalance(stashAddress);
            // 更新相应数据库记录
            dotService.updateAmountByAddress(stashAddress, balance, delegateAmount);

            if (delegateAmount.compareTo(BigDecimal.ZERO) == 0) {
                dotService.updateStakeStatus(stashAddress, StakeStatus.NO_STAKE.name());
            }
            dotService.updateTxStatus(tx.getId());
        });
        logger.info("DOT解绑质押更新程序结束");
    }
}
