package com.atoken.update.bnb.core;

import com.atoken.blockchain.utils.Final;
import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.*;
import com.atoken.update.entity.coin.account.bnb.BnbTokenBalance;
import com.atoken.update.entity.coin.account.bnb.BnbTokenTx;
import com.atoken.update.entity.coin.account.bnb.BnbTx;
import com.atoken.update.exception.IsolatedBlockException;
import com.atoken.update.exception.NodeException;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.service.coin.account.bnb.BnbTokenBalanceService;
import com.atoken.update.service.coin.account.bnb.BnbTokenTxService;
import com.atoken.update.service.coin.account.bnb.BnbTxService;
import com.atoken.update.util.LoggerUtil;
import com.atoken.update.util.VerifyUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Service("bnbBlockTxCore")
public class BnbBlockTxCore extends BlockTxCore {

    @Autowired
    private DingdingNotification dingdingNotification;
    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;
    private static Long notificationSendTime = 0L;

    @Autowired
    private BnbTokenBalanceService bnbTokenBalanceService;
    @Autowired
    private BnbTxService bnbTxService;
    @Autowired
    private BnbTokenTxService bnbTokenTxService;

    @Autowired
    public BnbBlockTxCore(
            @Qualifier("bnbBlockHandlerService") BlockHandlerService blockHandlerService,
            @Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public TransferMessage getTransferMessage() {
        return null;
    }

    @Override
    public TxAfterHandler txAfterHandler() {
        return null;
    }

    @Override
    public RollBackTxAfterHander rollBackTxAfterHander() {
        return null;
    }

    @Override
    public void blockTxHandler() {
        // // get unsync block from database
        List<Block> blockList = blockService.selectBlocks(blockchainConfig.getTablePrefix());
        logger.info("{} tx sync block:{}", blockchainConfig.getMainAsset(), blockList.size());
        // handler block
        blockList.forEach(block -> {
            long start = System.currentTimeMillis();
            try {
                logger.info("{} block:{} handler start", blockchainConfig.getMainAsset(), block.getHash());
                // nonIsolatedBlock(block.getHeight() - 1, block.getPrevious(), rollbackCount);
                // get tx by block
                List<Tx> txList = blockHandlerService.getBlockTx(block);
                long getBlockTxEnd = System.currentTimeMillis();
                logger.info("BNB blockTxHandler getBlockTx time :{}", getBlockTxEnd - start);

                logger.info("{} tx sync txlist size{}", blockchainConfig.getMainAsset(), txList.size());
                Final<Integer> relative = Final.newFinal(0);
                // handler tx
                txList.forEach(tx -> {
                    try {
                        // logger.info("{} tx sync height:{},hash:{}",blockchainConfig.getMainAsset(),block.getHeight(),block.getHash());
                        // 通过地址查询地址信息
                        List<Address> addressList = addressService.selectAddressByAddress(
                                blockchainConfig.getTablePrefix(), tx.getFromAddr(), tx.getToAddr());
                        if (CollectionUtils.isEmpty(addressList)) {
                            // logger.info("{} tx sync address empty from:{},to:{}",blockchainConfig.getMainAsset(),tx.getFromAddr(),tx.getToAddr());
                            return;
                        }
                        List<String> addrList = addressList.stream().map(Address::getAddr).collect(Collectors.toList());
                        boolean isToken = tx instanceof BnbTokenTx;
                        if (isToken) {
                            BnbTokenTx bnbTokenTx = BnbTokenTx.class.cast(tx);
                            String contractAddr = bnbTokenTx.getContractAddr();
                            Integer decimals = bnbTokenTx.getDecimals();
                            Map<String, BnbTokenBalance> map = Maps.newConcurrentMap();
                            Optional.ofNullable(bnbTokenBalanceService
                                    .getListByContractAddrAndAddrs(contractAddr, addrList))
                                    .ifPresent(l -> l.forEach(e -> map.put(e.getAddr(), e)));
                            addrList.forEach(addr -> {
                                // 获取代币余额
                                BigDecimal balance = blockHandlerService
                                        .getTokenBalance(contractAddr, addr, decimals);
                                if (map.containsKey(addr)) {
                                    Optional.ofNullable(map.get(addr)).ifPresent(e -> {
                                        e.setBalance(balance);
                                        bnbTokenBalanceService.updateById(e);
                                    });
                                } else {
                                    BnbTokenBalance tokenBalance = new BnbTokenBalance();
                                    //注意默认抓取有资产的代币默认从1变0
//                                    tokenBalance.setAdded((byte) 1);
                                    tokenBalance.setAdded((byte) 0);
                                    tokenBalance.setAddr(addr);
                                    tokenBalance.setContractAddr(contractAddr);
                                    tokenBalance.setDecimals(decimals);
                                    tokenBalance.setBalance(balance);
                                    bnbTokenBalanceService.saveOrUpdate(tokenBalance);
                                }
                            });
                            try {
                                bnbTokenTxService.removeUnconfirmByTxHash(bnbTokenTx.getTxHash());
                                bnbTokenTxService.saveOrUpdate(bnbTokenTx);
                            } catch (Exception e) {
                                logger.error("saveOrUpdate failed, bnbTokenTx: {}", bnbTokenTx, e);
                            }
                            relative.setType(1);
                        } else {
                            BnbTx bnbTx = (BnbTx) tx;
                            addressList.forEach(address -> {
                                BigDecimal balance = blockHandlerService.getAddressBalance(address.getAddr());
                                addressService.updateAmountByAddress(blockchainConfig.getTablePrefix(),
                                        address.getAddr(), balance);
                            });
                            try {
                                // save tx
                                logger.info("bnb tx sync save tx:{}", bnbTx);
                                bnbTxService.saveOrUpdate(bnbTx);
                            } catch (Exception e) {
                                logger.error("saveOrUpdate failed, bnbTx: {}", bnbTx, e);
                            }
                            relative.setType(1);
                        }
                    } catch (Exception e) {
                        logger.error("{} block tx sync error:{},height:{},hash:{}",
                                blockchainConfig.getMainAsset(), e.getMessage(), block.getHeight(), block.getHash());
                        dingdingNotification.txHandlerExceptionAlarm(
                                blockchainConfig.getMainAsset(), block.getHeight(),
                                tx.getHash(), LoggerUtil.parseErrorStacks(e));
                        syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, tx, e);
                        logger.error("save coin:{} block:{} tx:{} num: {} failed, exception: {}",
                                blockchainConfig.getMainAsset(), block.getHash(),
                                tx.getHash(), tx.getTxNum(), e.getLocalizedMessage());
                    }
                });
                long txEnd = System.currentTimeMillis();
                logger.info("BNB blockTxHandler txEnd time :{}", txEnd - getBlockTxEnd);

                Integer row = blockService.updateBlockSyncedById(blockchainConfig.getTablePrefix(),
                        block.getId(), relative.getType());
                VerifyUtil.numberNotZore(row, "update block sync failed");

                logger.info("{} tx block:{} handler complated", blockchainConfig.getMainAsset(), block.getHash());
            } catch (IsolatedBlockException e) {
                logger.error("{} block tx sync error:{},height:{},hash:{}", blockchainConfig.getMainAsset(), e.getMessage(), block.getHeight(), block.getHash());
                if (Instant.now().getEpochSecond()
                        - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.isolatedBlockExceptionAlarm(
                            blockchainConfig.getMainAsset(), block.getHeight() - 1,
                            LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (NodeException e) {
                logger.error("{} block tx sync error:{},height:{},hash:{}", blockchainConfig.getMainAsset(), e.getMessage(), block.getHeight(), block.getHash());
                // 邮件发送间隔时间， 默认一个小时
                if (Instant.now().getEpochSecond()
                        - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.nodeExceptionAlarm(blockchainConfig.getMainAsset(),
                            block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (Exception e) {
                if (Instant.now().getEpochSecond()
                        - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.blockHandlerExceptionAlarm(blockchainConfig.getMainAsset(),
                            block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, null, e);
                logger.error("save coin:{} block:{} failed, exception: {}",
                        blockchainConfig.getMainAsset(), block.getHash(), e.getLocalizedMessage());
            }

            long end = System.currentTimeMillis();
            logger.info("BNB blockTxHandler time start:{}", end - start);

        });
    }

    @Override
    public void nonIsolatedBlock(Integer blockHeight, String blockHash, Integer count) {
    }
}
