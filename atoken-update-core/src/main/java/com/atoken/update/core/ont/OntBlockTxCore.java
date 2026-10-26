package com.atoken.update.core.ont;

import com.atoken.update.entity.ont.Address;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.Tx;
import com.atoken.update.exception.IsolatedBlockException;
import com.atoken.update.exception.NodeException;
import com.atoken.update.notification.DingdingNotification;
import com.atoken.update.service.ont.*;
import com.atoken.update.util.LoggerUtil;
import com.atoken.update.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class OntBlockTxCore {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OntBlockService ontBlockService;
    @Autowired
    private OntBlockHandlerService ontBlockHandlerService;
    @Autowired
    private OntAddressService ontAddressService;
    @Autowired
    private OntTxService ontTxService;
    @Autowired
    private OntSyncErrorService ontSyncErrorService;
    @Value("${blockchain.max-rollback-count: 20}")
    private Integer maxRollbackCount;
    private String asset = "ont";
    @Autowired
    private DingdingNotification dingdingNotification;
    @Value("${atoken.notification.duration: 3600}")
    private Long notificationSendDuration;
    @Autowired
    private OntTokenService ontTokenService;
    private static Long notificationSendTime = 0l;

    public void blockTxHandler() {
        // get unsync block from database
        List<Block> blockList = ontBlockService.selectBlocks();
        Integer rollbackCount = 0;

        // handler block
        blockList.forEach(block -> {
            try {
                logger.info("block:{} handler start", block.getHash());
                nonIsolatedBlock(block.getHeight() - 1, block.getPrevious(), rollbackCount);

                // get tx by block
                List<Tx> txList = ontBlockHandlerService.getBlockTx(block);
                Integer relative = 0;
                // handler tx
                if (Objects.nonNull(txList)) {
                    for (Tx tx : txList) {
                        try {
                            List<Address> addressList = ontAddressService.selectAddressByAddress(tx.getFromAddr(), tx.getToAddr());
                            Map<String, Map<String, BigDecimal>> addressBalanceMap = new HashMap<>();
                            if(!CollectionUtils.isEmpty(addressList)) {
                                addressList.forEach(address -> {
                                    Map<String, BigDecimal> balanceMap = ontBlockHandlerService.getAddressBalance(address.getAddr(), ontTokenService);
                                    addressBalanceMap.put(address.getAddr(), balanceMap);
                                });
                            }
                            // save tx
                            relative = relative | ontTxService.saveTx(addressList, tx, addressBalanceMap);
                        } catch (Exception e) {
                            dingdingNotification.txHandlerExceptionAlarm(asset, block.getHeight(), tx.getHash(), LoggerUtil.parseErrorStacks(e));
                            ontSyncErrorService.saveError(block, tx, e);
                            logger.error("save ont block:{} tx:{} num: {} failed, exception: {}", block.getHash(), tx.getHash(), tx.getTxNum(), e.getLocalizedMessage());
                        }
                    }
                }
                Integer row = ontBlockService.updateBlockSyncedByHash(block.getHash(), relative);
                VerifyUtil.numberNotZore(row, "update block sync failed");

                logger.info("block:{} handler complated", block.getHash());
            } catch (IsolatedBlockException e) {
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.isolatedBlockExceptionAlarm(asset, block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (NodeException e) {
                // 邮件发送间隔时间， 默认一个小时
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.nodeExceptionAlarm(asset, block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                throw e;
            } catch (Exception e) {
                if (Instant.now().getEpochSecond() - notificationSendTime >= notificationSendDuration) {
                    dingdingNotification.blockHandlerExceptionAlarm(asset, block.getHeight() - 1, LoggerUtil.parseErrorStacks(e));
                    notificationSendTime = Instant.now().getEpochSecond();
                }
                ontSyncErrorService.saveError(block, null, e);
                logger.error("save ont block:{} failed, exception: {}", block.getHash(), e.getLocalizedMessage());
            }
        });
    }

    @Transactional
    public void nonIsolatedBlock(Integer blockHeight, String blockHash, Integer count) {
        try {
            if (count >= maxRollbackCount) {
                throw new IsolatedBlockException("回滚超过最大限制" + maxRollbackCount);
            }
            Block parentBlock = ontBlockService.selectBlockByHeight(blockHeight);

            if (Objects.nonNull(parentBlock) && !parentBlock.getHash().equals(blockHash)) {
                logger.warn("handler isolated block:{} start", blockHash);
                ontTxService.rollBackTx(parentBlock);
                ontBlockService.deleteBlockByBlockHash(parentBlock.getHash());
                List<Block> blockList = ontBlockHandlerService.getBlocks(Arrays.asList(blockHeight));
                Integer row = ontBlockService.insertBlock(blockList.get(0));
                VerifyUtil.numberNotZore(row, "回滚用户资产表失败");
                String blockHash_1 = ontBlockHandlerService.getBlockHash(parentBlock.getHeight() - 1);
                nonIsolatedBlock(parentBlock.getHeight() - 1, blockHash_1, ++count);
            }
        } catch (Exception e) {
            throw new IsolatedBlockException(e.getMessage());
        }
    }
}
