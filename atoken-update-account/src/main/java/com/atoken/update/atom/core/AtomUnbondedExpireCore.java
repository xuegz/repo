package com.atoken.update.atom.core;

import com.atoken.blockchain.http.request.account.atom.AtomClient;
import com.atoken.update.atom.cache.AtomUnbondingTimeCache;
import com.atoken.update.atom.common.AddressAmountHander;
import com.atoken.update.entity.atom.AtomAmountInfo;
import com.atoken.update.atom.service.AtomBlockHandlerService;
import com.atoken.update.entity.atom.AtomDelegateDetail;
import com.atoken.update.entity.atom.AtomTx;
import com.atoken.update.service.atom.AtomService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

//@Service("atomUnbondedExpireCore")
public class AtomUnbondedExpireCore implements AtokenLogger {
    @Autowired
    private AtomClient atomClient;
    @Autowired
    private AtomService atomService;
    @Autowired
    private AtomUnbondingTimeCache atomUnbondingTimeCache;
    @Autowired
    private AtomBlockHandlerService atomBlockHandlerService;

    public void unbondedExpireHandler() {
        logger.info("解绑质押更新程序开始");
        // 获取链上的解绑时间
        Long unbondingTime = atomUnbondingTimeCache.get(AtomUnbondingTimeCache.CACHE_KEY);
        // 获取需要解绑的交易
        List<AtomTx> atomTxes = atomService.selectUnbondedAtomTxs(Instant.now().getEpochSecond(), unbondingTime);

        atomTxes.forEach(atomTx -> {
            try{
                logger.info("tx: {}, 解绑开始", atomTx.getHash());
                BigDecimal amount = atomBlockHandlerService.getAddressBalance(atomTx.getFromAddr());
                // 获取金额信息
                AtomAmountInfo atomAmountInfo = AddressAmountHander.getAddressAmountInfo(atomTx.getFromAddr(), atomClient);
                AtomAmountInfo atomDelegateDetailInfo = AddressAmountHander.getDelegateDetailAmount(atomTx.getFromAddr(), atomTx.getToAddr(), atomClient);
                AtomDelegateDetail atomDelegateDetail = new AtomDelegateDetail();
                atomDelegateDetail.setAddr(atomTx.getFromAddr());
                atomDelegateDetail.setValidatorAddr(atomTx.getToAddr());
                atomDelegateDetail.setDelegateAmount(atomDelegateDetailInfo.getDelegationAmount());
                atomDelegateDetail.setUnbondingAmount(atomDelegateDetailInfo.getUnBondingAmount());
                logger.info("tx: {}, amount: {}, delegateAmount: {}, unbondingAmount: {}", atomTx.getHash(), amount, atomAmountInfo.getDelegationAmount(), atomAmountInfo.getUnBondingAmount());
                // 更新数据库解绑信息
                atomService.unbondingExpire(atomTx.getId(), atomTx.getFromAddr(), atomTx.getToAddr(), amount, atomAmountInfo, atomDelegateDetail);
            }catch (Exception e) {
                logger.error("解绑质押更新失败，地址：{}", atomTx.getFromAddr());
            }
        });

        logger.info("解绑质押更新程序结束");

    }
}
