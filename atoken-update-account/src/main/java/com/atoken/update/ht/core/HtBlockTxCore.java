package com.atoken.update.ht.core;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.utils.Final;
import com.atoken.update.core.coin.account.BlockTxCore;
import com.atoken.update.entity.coin.account.*;
import com.atoken.update.entity.coin.account.ht.HtTokenBalance;
import com.atoken.update.entity.coin.account.ht.HtTokenTx;
import com.atoken.update.entity.coin.account.ht.HtTx;
import com.atoken.update.exception.IsolatedBlockException;
import com.atoken.update.exception.NodeException;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.service.coin.account.ht.HtTokenBalanceService;
import com.atoken.update.service.coin.account.ht.HtTokenTxService;
import com.atoken.update.service.coin.account.ht.HtTxService;
import com.atoken.update.util.LoggerUtil;
import com.atoken.update.util.VerifyUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Service("htBlockTxCore")
public class HtBlockTxCore extends BlockTxCore {

    @Autowired
    private DingdingNotification dingdingNotification;
    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;
    private static Long notificationSendTime = 0L;

    @Autowired
    private HtTokenBalanceService htTokenBalanceService;
    @Autowired
    private HtTxService htTxService;
    @Autowired
    private HtTokenTxService htTokenTxService;

    @Autowired
    public HtBlockTxCore(
            @Qualifier("htBlockHandlerService") BlockHandlerService blockHandlerService,
            @Qualifier("htBlockchainConfig") BlockchainConfig blockchainConfig) {
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
        // handler block
        blockList.forEach(block -> {
            try {
                logger.info("block:{} handler start", block.getHash());
                // get tx by block
                List<Tx> txList = blockHandlerService.getBlockTx(block);
                Final<Integer> relative = Final.newFinal(0);
                // handler tx
                txList.forEach(tx -> {
                    try {
                        // 通过地址查询地址信息
                        List<Address> addressList = addressService.selectAddressByAddress(
                                blockchainConfig.getTablePrefix(), tx.getFromAddr(),
                                tx.getToAddr());
                        if (CollectionUtils.isEmpty(addressList)) {
                            return;
                        }
                        List<String> addrList = Optional.ofNullable(addressList).map(Collection::stream)
                                .map(s -> s.map(Address::getAddr).collect(Collectors.toList()))
                                .orElse(Collections.emptyList());
                        boolean isToken = tx instanceof HtTokenTx;
                        if (isToken) {
                            HtTokenTx htTokenTx = HtTokenTx.class.cast(tx);
                            String contractAddr = htTokenTx.getContractAddr();
                            Integer decimals = htTokenTx.getDecimals();
                            Map<String, HtTokenBalance> map = Maps.newConcurrentMap();
                            Optional.ofNullable(htTokenBalanceService
                                    .getListByContractAddrAndAddrs(contractAddr, addrList))
                                    .ifPresent(
                                            l -> l.forEach(e -> map.put(e.getAddr(), e)));
                            addrList.forEach(addr -> {
                                // 获取代币余额
                                BigDecimal balance = blockHandlerService
                                        .getTokenBalance(contractAddr, addr, decimals);
                                if (map.containsKey(addr)) {
                                    Optional.ofNullable(map.get(addr)).ifPresent(e -> {
                                        e.setBalance(balance);
                                        htTokenBalanceService.updateById(e);
                                    });
                                } else {
                                    HtTokenBalance tokenBalance = new HtTokenBalance();
                                    //默认代币显示由1变为0
//                                    tokenBalance.setAdded((byte) 1);
                                    tokenBalance.setAdded((byte) 0);
                                    tokenBalance.setAddr(addr);
                                    tokenBalance.setContractAddr(contractAddr);
                                    tokenBalance.setDecimals(decimals);
                                    tokenBalance.setBalance(balance);
                                    htTokenBalanceService.saveOrUpdate(tokenBalance);
                                }
                            });
                            try {
                                htTokenTxService.removeUnconfirmByTxHash(htTokenTx.getTxHash());
                                htTokenTxService.saveOrUpdate(htTokenTx);
                            } catch (Exception e) {
                                logger.error("saveOrUpdate failed, htTokenTx: {}",
                                        JSON.toJSONString(htTokenTx), e);
                            }
                            relative.setType(1);
                        } else {
                            HtTx htTx = HtTx.class.cast(tx);
                            addressList.forEach(address -> {
                                BigDecimal balance = blockHandlerService
                                        .getAddressBalance(address.getAddr());
                                addressService.updateAmountByAddress(
                                        blockchainConfig.getTablePrefix(),
                                        address.getAddr(), balance);
                            });
                            try {
                                // save tx
                                htTxService.saveOrUpdate(htTx);
                            } catch (Exception e) {
                                logger.error("saveOrUpdate failed, htTx: {}",
                                        JSON.toJSONString(htTx), e);
                            }
                            relative.setType(1);
                        }
                    } catch (Exception e) {
                        dingdingNotification.txHandlerExceptionAlarm(
                                blockchainConfig.getMainAsset(), block.getHeight(),
                                tx.getHash(), LoggerUtil.parseErrorStacks(e));
                        syncErrorService.saveError(blockchainConfig.getTablePrefix(), block,
                                tx, e);
                        logger.error(
                                "save coin:{} block:{} tx:{} num: {} failed, exception: {}",
                                blockchainConfig.getMainAsset(), block.getHash(),
                                tx.getHash(), tx.getTxNum(), e.getLocalizedMessage());
                    }
                });
                Integer row = blockService.updateBlockSyncedById(blockchainConfig.getTablePrefix(),
                        block.getId(), relative.getType());
                VerifyUtil.numberNotZore(row, "update block sync failed");

                logger.info("block:{} handler complated", block.getHash());
            } catch (IsolatedBlockException e) {
                if (Instant.now().getEpochSecond()
                        - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.isolatedBlockExceptionAlarm(
                            blockchainConfig.getMainAsset(), block.getHeight() - 1,
                            LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (NodeException e) {
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
        });
    }

    @Override
    @Transactional
    public void nonIsolatedBlock(Integer blockHeight, String blockHash, Integer count) {
        try {
            if (count >= blockchainConfig.getMaxRollbackCount()) {
                throw new IsolatedBlockException(
                        "回滚超过最大限制" + blockchainConfig.getMaxRollbackCount());
            }
            List<Block> parentBlocks = blockService
                    .selectBlockByHeight(blockchainConfig.getTablePrefix(), blockHeight);
            if (Objects.nonNull(parentBlocks)) {
                int size = parentBlocks.size();
                Block parentBlock = null;
                if (size > 1) {
                    for (Block block : parentBlocks) {
                        if (Objects.nonNull(block)) {
                            if (!block.getHash().equals(blockHash)) {
                                // 删除孤块
                                logger.warn("handler isolated block:{} start", blockHash);
                                htTokenTxService.rollBackTx(parentBlock);
                                htTxService.rollBackTx(blockchainConfig.getTablePrefix(), block,
                                        rollBackTxAfterHander());
                                blockService.deleteBlockByBlockHash(
                                        blockchainConfig.getTablePrefix(), block.getHash());
                            } else {
                                parentBlock = block;
                            }
                        }
                    }
                    if (Objects.isNull(parentBlock)) {
                        // 如果此块高下的数据都被删除，那么从链上重新获取块信息
                        List<Block> blockList =
                                blockHandlerService.getBlocks(Arrays.asList(blockHeight));
                        Integer row = blockService.insertBlock(blockchainConfig.getTablePrefix(),
                                blockList.get(0));
                        VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                        String blockHash_1 =
                                blockHandlerService.getBlockHash(parentBlock.getHeight() - 1);
                        nonIsolatedBlock(parentBlock.getHeight() - 1, blockHash_1, ++count);
                    }
                } else if (size == 1) {
                    parentBlock = parentBlocks.get(0);
                    if (Objects.nonNull(parentBlock) && !parentBlock.getHash().equals(blockHash)) {
                        logger.warn("handler isolated block:{} start", blockHash);
                        htTokenTxService.rollBackTx(parentBlock);
                        htTxService.rollBackTx(blockchainConfig.getTablePrefix(), parentBlock,
                                rollBackTxAfterHander());
                        blockService.deleteBlockByBlockHash(blockchainConfig.getTablePrefix(),
                                parentBlock.getHash());
                        List<Block> blockList =
                                blockHandlerService.getBlocks(Arrays.asList(blockHeight));
                        Integer row = blockService.insertBlock(blockchainConfig.getTablePrefix(),
                                blockList.get(0));
                        VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                        String blockHash_1 =
                                blockHandlerService.getBlockHash(parentBlock.getHeight() - 1);
                        nonIsolatedBlock(parentBlock.getHeight() - 1, blockHash_1, ++count);
                    }
                }
            }

        } catch (Exception e) {
            throw new IsolatedBlockException(e.getMessage());
        }
    }

}
