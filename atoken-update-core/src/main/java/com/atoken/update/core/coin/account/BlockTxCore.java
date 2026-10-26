package com.atoken.update.core.coin.account;

import com.atoken.update.entity.coin.account.*;
import com.atoken.update.enums.CoinTypeEnum;
import com.atoken.update.exception.IsolatedBlockException;
import com.atoken.update.exception.NodeException;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.coin.account.*;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.LoggerUtil;
import com.atoken.update.util.VerifyUtil;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public abstract class BlockTxCore implements AtokenLogger {
    @Autowired
    protected AccountBlockService blockService;
    protected BlockHandlerService blockHandlerService;
    protected BlockchainConfig blockchainConfig;
    @Autowired
    protected AccountAddressService addressService;
    @Autowired
    private AccountTxService txService;
    @Autowired
    protected AccountSyncErrorService syncErrorService;
    @Autowired
    private DingdingNotification dingdingNotification;
    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;
    private static Long notificationSendTime = 0L;

    private List<String> polkaCoins = Lists.newArrayList(CoinTypeEnum.dot.getCoinName(),CoinTypeEnum.ksm.getCoinName(),CoinTypeEnum.kar.getCoinName());

    public BlockTxCore(BlockHandlerService blockHandlerService, BlockchainConfig blockchainConfig) {
        this.blockchainConfig =blockchainConfig;
        this.blockHandlerService = blockHandlerService;
    }

    public void blockTxHandler() {
        // get unsync block from database
        List<Block> blockList = blockService.selectBlocks(blockchainConfig.getTablePrefix());
        logger.info("{} block list size:{}",blockchainConfig.getMainAsset(),blockList.size());
        Integer rollbackCount = 0;

        // handler block
        blockList.forEach(block -> {
            try {
                logger.info("{} tx sync block:{} handler start,height:{}",blockchainConfig.getMainAsset(), block.getHash(),block.getHeight());
                nonIsolatedBlock(block.getHeight() - 1, block.getPrevious(), rollbackCount);

                // get tx by block
                logger.info("{} tx sync height:{}",blockchainConfig.getMainAsset(),block.getHeight());
                List<Tx> txList = blockHandlerService.getBlockTx(block);
                logger.info("{} tx sync tx list size:{}",blockchainConfig.getMainAsset(),txList.size());
                Integer relative = 0;
                // handler tx
                if (Objects.nonNull(txList)) {
                    for (Tx tx : txList) {
                        try {
                            // 通过地址查询地址信息
                            List<Address> addressList = Lists.newArrayList();
                            addressList = selectAddress(tx.getFromAddr(), addressList);
                            addressList = selectAddress(tx.getToAddr(), addressList);

                            if (CollectionUtils.isEmpty(addressList)){
                                continue;
                            }
                            // 获取地址余额信息
                            Map<String, BigDecimal> addressBalanceMap = new HashMap<>();
                            addressList.forEach(address -> {
                                BigDecimal balance = BigDecimal.ZERO;
                                if (polkaCoins.contains(blockchainConfig.getMainAsset())){
                                    balance = this.blockHandlerService.getAddressFreeBalance(address.getAddr());
                                }else {
                                    balance = this.blockHandlerService.getAddressBalance(address.getAddr());
                                }

                                addressBalanceMap.put(address.getAddr(), balance);
                            });

                            // save tx
                            relative = relative | txService.saveTx(blockchainConfig.getTablePrefix(), addressList, tx, addressBalanceMap, txAfterHandler(), getTransferMessage());
                            logger.info("{} save tx result:{}",blockchainConfig.getMainAsset(),relative);
                        } catch (Exception e) {
                            dingdingNotification.txHandlerExceptionAlarm(blockchainConfig.getMainAsset(), block.getHeight(), tx.getHash(), LoggerUtil.parseErrorStacks(e));
                            syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, tx, e);
                            logger.error("save coin:{} block:{} tx:{} num: {} failed, exception: {}",blockchainConfig.getMainAsset(), block.getHash(), tx.getHash(), tx.getTxNum(), e.getLocalizedMessage());
                        }
                    }
                }
                Integer row = blockService.updateBlockSyncedById(blockchainConfig.getTablePrefix(), block.getId(), relative);
                VerifyUtil.numberNotZore(row, "update block sync failed");

                logger.info("{} block:{} handler complated",blockchainConfig.getMainAsset(), block.getHash());
            } catch (IsolatedBlockException e) {
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.isolatedBlockExceptionAlarm(blockchainConfig.getMainAsset(), block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (NodeException e) {
                // 邮件发送间隔时间， 默认一个小时
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.nodeExceptionAlarm(blockchainConfig.getMainAsset(), block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (Exception e) {
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.blockHandlerExceptionAlarm(blockchainConfig.getMainAsset(), block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, null, e);
                logger.error("save coin:{} block:{} failed, exception: {}",blockchainConfig.getMainAsset(), block.getHash(), e.getLocalizedMessage());
            }
        });
    }

    private List<Address> selectAddress(String addr, List<Address> addressList) {
        if (StringUtils.isNotEmpty(addr)) {
            List<Address> addresses = addressService.selectaddressbyaddr(blockchainConfig.getTablePrefix(), addr);
            if (!CollectionUtils.isEmpty(addresses)) {
                addressList.addAll(addresses);
            }
        }
        return addressList;
    }

    @Transactional
    public void nonIsolatedBlock(Integer blockHeight, String blockHash, Integer count) {
        try {
            if (count >= blockchainConfig.getMaxRollbackCount()) {
                throw new IsolatedBlockException("回滚超过最大限制" + blockchainConfig.getMaxRollbackCount());
            }
            List<Block> parentBlocks = blockService.selectBlockByHeight(blockchainConfig.getTablePrefix(), blockHeight);
            if (Objects.nonNull(parentBlocks)) {
                int size = parentBlocks.size();
                Block parentBlock = null;
                if (size > 1) {
                    for (Block block : parentBlocks) {
                        if (Objects.nonNull(block)) {
                            if (!block.getHash().equals(blockHash)) {
                                // 删除孤块
                                logger.warn("handler isolated block:{} start", blockHash);
                                txService.rollBackTx(blockchainConfig.getTablePrefix(), block, rollBackTxAfterHander());
                                blockService.deleteBlockByBlockHash(blockchainConfig.getTablePrefix(), block.getHash());
                            } else {
                                parentBlock = block;
                            }
                        }
                    }
                    if (Objects.isNull(parentBlock)) {
                        // 如果此块高下的数据都被删除，那么从链上重新获取块信息
                        List<Block> blockList = blockHandlerService.getBlocks(Arrays.asList(blockHeight));
                        Integer row = blockService.insertBlock(blockchainConfig.getTablePrefix(), blockList.get(0));
                        VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                        String blockHash_1 = blockHandlerService.getBlockHash(parentBlock.getHeight() - 1);
                        nonIsolatedBlock(parentBlock.getHeight() - 1, blockHash_1, ++count);
                    }
                } else if(size == 1) {
                    parentBlock = parentBlocks.get(0);
                    if (Objects.nonNull(parentBlock) && !parentBlock.getHash().equals(blockHash)) {
                        logger.warn("handler isolated block:{} start", blockHash);
                        txService.rollBackTx(blockchainConfig.getTablePrefix(), parentBlock, rollBackTxAfterHander());
                        blockService.deleteBlockByBlockHash(blockchainConfig.getTablePrefix(), parentBlock.getHash());
                        List<Block> blockList = blockHandlerService.getBlocks(Arrays.asList(blockHeight));
                        Integer row = blockService.insertBlock(blockchainConfig.getTablePrefix(), blockList.get(0));
                        VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                        String blockHash_1 = blockHandlerService.getBlockHash(parentBlock.getHeight() - 1);
                        nonIsolatedBlock(parentBlock.getHeight() - 1, blockHash_1, ++count);
                    }
                }
            }

        } catch (Exception e) {
            throw new IsolatedBlockException(e.getMessage());
        }
    }

    public abstract TransferMessage getTransferMessage();

    public abstract TxAfterHandler txAfterHandler();

    public abstract RollBackTxAfterHander rollBackTxAfterHander();
}
