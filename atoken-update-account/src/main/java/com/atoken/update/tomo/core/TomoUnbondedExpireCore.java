package com.atoken.update.tomo.core;

import com.atoken.blockchain.http.request.account.tomo.TomoClient;
import com.atoken.blockchain.http.request.account.tomo.TomoVotedCandidate;
import com.atoken.blockchain.http.request.account.tomo.TomoVotedCandidates;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.service.coin.account.AccountStakingService;
import com.atoken.update.tomo.service.TomoBlockHandlerService;
import com.atoken.update.util.AtokenLogger;
import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("tomoUnbondedExpireCore")
public class TomoUnbondedExpireCore implements AtokenLogger {
    @Autowired
    private TomoClient tomoClient;
    @Autowired
    private AccountStakingService stakingService;
    @Autowired
    private TomoBlockHandlerService tomoBlockHandlerService;
    @Autowired
    @Qualifier("tomoBlockchainConfig")
    private BlockchainConfig blockchainConfig;
    @Value("${blockchain.tomo.unbounded-time}")
    private Long unboundedTime;

    public void unbondedExpireHandler() {
        logger.info("解绑质押更新程序开始");
        Long overTime = Instant.now().getEpochSecond() - unboundedTime;
        // 查询需要处理的解绑交易
        List<Tx> unboundedTxes = stakingService.selectUnbondedTxs(TomoValidatorInfoCore.TABLE_PREFIX, overTime);
        Set<String> voterAddresses = unboundedTxes.stream().map(Tx::getFromAddr).collect(Collectors.toSet());
        voterAddresses.forEach(voterAddresse -> {
            try {

                List<Tx> txes = unboundedTxes.stream().filter(unboundedTxe -> unboundedTxe.getFromAddr().equals(voterAddresse)).collect(Collectors.toList());
                List<TomoVotedCandidate> tomoVotedCandidateList = new ArrayList<>();
                for (int i = 1; ; i++) {
                    // 获取用户质押信息列表
                    TomoVotedCandidates tomoVotedCandidates = tomoClient.getVotedCandidates(voterAddresse, i, 20);
                    if (Objects.isNull(tomoVotedCandidates) || CollectionUtils.isEmpty(tomoVotedCandidates.getItems())) {
                        break;
                    }
                    tomoVotedCandidateList.addAll(tomoVotedCandidates.getItems());
                }
                Map<String, TomoVotedCandidate> tomoVotedCandidateMap = tomoVotedCandidateList.stream().collect(Collectors.toMap(TomoVotedCandidate::getCandidate, Functions.identity()));
                txes.forEach(tx -> {
                    try {
                        logger.info("tx: {}, 解绑开始", tx.getHash());
                        // 通过地址获取质押信息
                        TomoVotedCandidate tomoVotedCandidate = tomoVotedCandidateMap.get(tx.getToAddr());
                        BigDecimal delegateAmount = BigDecimal.ZERO;
                        if(Objects.nonNull(tomoVotedCandidate)) {
                            delegateAmount = DataUtil.formatAmount(tomoVotedCandidate
                                    .getCapacity(), CoinEnum.tomo.decimals());
                        }
                        stakingService.unbondedExpireHandler(blockchainConfig.getTablePrefix(), tx, delegateAmount);
                    } catch (Exception e) {
                        logger.error("解绑质押更新失败，地址：{}", tx.getFromAddr());
                    }
                });
            } catch (Exception e) {
                logger.error("解绑质押更新失败，地址：{}", voterAddresse);
            }
        });
        logger.info("解绑质押更新程序结束");

    }
}
