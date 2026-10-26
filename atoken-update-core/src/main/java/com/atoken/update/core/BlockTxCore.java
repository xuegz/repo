package com.atoken.update.core;

import com.atoken.update.entity.*;
import com.atoken.update.exception.IsolatedBlockException;
import com.atoken.update.exception.NodeException;
import com.atoken.update.handler.RollBackTxAfterHander;
import com.atoken.update.handler.TxAfterHandler;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.*;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.LoggerUtil;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public abstract class BlockTxCore implements AtokenLogger {
    private BlockHandlerService blockHandlerService;
    private BlockchainConfig blockchainConfig;
    private TxService txService;

    @Autowired
    private BlockService blockService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private SyncErrorService syncErrorService;
    @Autowired
    private DingdingNotification dingdingNotification;

    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;

    private static Long notificationSendTime = 0L;

    public BlockTxCore(BlockHandlerService blockHandlerService, BlockchainConfig blockchainConfig, TxService txService) {
        this.blockchainConfig = blockchainConfig;
        this.blockHandlerService = blockHandlerService;
        this.txService = txService;
    }

    public void blockTxHandler() {
        // get unsync block from database
        List<Block> blockList = blockService.selectBlocks(blockchainConfig.getDataBase());
        Integer rollbackCount = 0;

        // handler block
        blockList.forEach(block -> {
            try {
                logger.info("block:{} handler start", block.getHash());
                nonIsolatedBlock(block.getHeight() - 1, block.getPrevious(), rollbackCount);

                // get tx by block
                List<Tx> txList = blockHandlerService.getBlockTx(block);
                Integer relative = 0;
                // handler tx
                if (Objects.nonNull(txList)) {
                    for (Tx tx : txList) {
                        try {
                            // 通过地址查询地址信息
                            List<Address> addressList = addressService.selectAddressByAddress(blockchainConfig.getDataBase(), tx.getFromAddr(), tx.getToAddr());
                            // 获取地址余额信息
                            Map<String, BigDecimal> addressBalanceMap = new HashMap<>();
                            if (CollectionUtils.isEmpty(addressList)){
                                continue;
                            }

                            addressList.forEach(address -> {
                                BigDecimal balance = blockHandlerService.getAddressBalance(address.getAddr());
                                addressBalanceMap.put(address.getAddr(), balance);
                            });
                            // save tx
                            relative = relative | txService.saveTx(blockchainConfig.getDataBase(), addressList, tx, addressBalanceMap, txAfterHandler(), getTransferMessage());
                        } catch (Exception e) {
                            dingdingNotification.txHandlerExceptionAlarm(blockchainConfig.getMainAsset(), block.getHeight(), tx.getHash(), LoggerUtil.parseErrorStacks(e));
                            syncErrorService.saveError(blockchainConfig.getDataBase(), block, tx, e);
                            logger.error("save ont block:{} tx:{} num: {} failed, exception: {}", block.getHash(), tx.getHash(), tx.getTxNum(), e.getLocalizedMessage());
                        }
                    }
                }
                Integer row = blockService.updateBlockSyncedByHash(blockchainConfig.getDataBase(), block.getHash(), relative);
                VerifyUtil.numberNotZore(row, "update block sync failed");

                logger.info("block:{} handler complated", block.getHash());
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
                syncErrorService.saveError(blockchainConfig.getDataBase(), block, null, e);
                logger.error("save ont block:{} failed, exception: {}", block.getHash(), e.getLocalizedMessage());
            }
        });
    }

    @Transactional
    public void nonIsolatedBlock(Integer blockHeight, String blockHash, Integer count) {
        try {
            if (count >= blockchainConfig.getMaxRollbackCount()) {
                throw new IsolatedBlockException("回滚超过最大限制" + blockchainConfig.getMaxRollbackCount());
            }
            Block parentBlock = blockService.selectBlockByHeight(blockchainConfig.getDataBase(), blockHeight);

            if (Objects.nonNull(parentBlock) && !parentBlock.getHash().equals(blockHash)) {
                logger.warn("handler isolated block:{} start", blockHash);
                txService.rollBackTx(blockchainConfig.getDataBase(), parentBlock, rollBackTxAfterHander());
                blockService.deleteBlockByBlockHash(blockchainConfig.getDataBase(), parentBlock.getHash());
                List<Block> blockList = blockHandlerService.getBlocks(Arrays.asList(blockHeight));
                Integer row = blockService.insertBlock(blockchainConfig.getDataBase(), blockList.get(0));
                VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                String blockHash_1 = blockHandlerService.getBlockHash(parentBlock.getHeight() - 1);
                nonIsolatedBlock(parentBlock.getHeight() - 1, blockHash_1, ++count);
            }
        } catch (Exception e) {
            throw new IsolatedBlockException(e.getMessage());
        }
    }

    public abstract TransferMessage getTransferMessage();

    public abstract TxAfterHandler txAfterHandler();

    public abstract RollBackTxAfterHander rollBackTxAfterHander();
}
