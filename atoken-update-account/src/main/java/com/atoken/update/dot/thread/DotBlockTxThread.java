package com.atoken.update.dot.thread;

import com.atoken.blockchain.http.request.account.dot.DotBlockInfo;
import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.DotTxType;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.Tx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.service.SyncErrorService;
import com.atoken.update.thread.BaseThread;
import org.apache.commons.codec.binary.Hex;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.Address;
import org.polkadot.types.type.Balance;
import org.polkadot.types.type.EventRecord;
import org.polkadot.types.type.Extrinsic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


/**
 * 重构之后实现 DotSidecarBlockHandlerService
 */
@Deprecated
@Component
public class DotBlockTxThread extends BaseThread<TxInfo, Tx> {
    private DotBlockInfo dotBlockInfo;
    private Map<Integer, EventRecord> eventRecordMap;
    private Map<Integer, Integer> stateMap;
    @Autowired
    private DotClient dotClient;

    @Autowired
    private SyncErrorService syncErrorService;

    @Autowired
    public DotBlockTxThread(@Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }

    @Override
    public void execute() {
        this.dotBlockInfo = dotClient.getBlock(this.block.getHash()).getResult();
        Vector<EventRecord> eventRecords = null;
        try {
            // 从链上获取该高度的事件信息
            eventRecords = dotClient.getEvent(this.block.getHash());
        } catch (Exception e) {
            logger.error(e.toString());
        }
        // 需要初始化一次，事件和交易的状态
        this.eventRecordMap = new HashMap<>();
        this.stateMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(eventRecords)) {
            eventRecords.forEach(eventRecord -> {
                // TODO 查看这些值是否有变化
                if ("0x0202".equals(eventRecord.getEvent().getIndex().toHex())) {
                    eventRecordMap.put(eventRecord.getPhase().asApplyExtrinsic().intValue(), eventRecord);
                } else if ("system".equals(eventRecord.getEvent().getSection())) {
                    stateMap.put(eventRecord.getPhase().asApplyExtrinsic().intValue(), "ExtrinsicSuccess".equals(eventRecord.getEvent().getMethod()) ? 1 : 0);
                }
            });
        }
        if (this.dotBlockInfo.getBlock().getExtrinsics() == null) {
            this.dotBlockInfo.getBlock().setExtrinsics(new ArrayList<>());
        }
        // TODO dot第一笔第二笔交易是固定交易所以要跳过，目前需要调研，看看是否是
        // 交易放入队列
        this.countDownLatch = new CountDownLatch(this.dotBlockInfo.getBlock().getExtrinsics().size() - 2);
        for (int i = 2, len = this.dotBlockInfo.getBlock().getExtrinsics().size(); i < len; i++) {
            try {
                txQueue.put(new TxInfo(i, this.dotBlockInfo.getBlock().getExtrinsics().get(i)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Runnable runnable() {
        return () -> {
            TxInfo txInfo = null;
            Tx tx = null;
            while (true) {
                try {
                    // 从队列里面取交易
                    txInfo = txQueue.take();
                    logger.info("dot tx:{} heandler start", txInfo.getTx());
                    // 解析交易
                    Extrinsic extrinsic = new Extrinsic(txInfo.getTx());
                    EventRecord eventRecord = eventRecordMap.get(txInfo.getIndex());
                    String txType = Hex.encodeHexString(extrinsic.getCallIndex());
                    // 只解析我们需要的交易
                    if (DotTxType.TRANSFER.isTxType(txType)
                            || DotTxType.BOND.isTxType(txType)
                            || DotTxType.NOMINATE.isTxType(txType)
                            || DotTxType.CHILL.isTxType(txType)
                            || DotTxType.UNBOND.isTxType(txType)
                            || DotTxType.BOND_EXTRA.isTxType(txType)) {
                        // 根据我们之前加载的事件获取手续费
                        BigDecimal fee = BigDecimal.ZERO;
                        if (Objects.nonNull(eventRecord)) {
                            Balance balance = (Balance) eventRecord.getEvent().getData().get(3);
                            fee = DataUtil.formatAmount(new BigDecimal(balance.toBn()), CoinEnum.dot.decimals());
                        }
                        DotTxType.ExtrinsicArgs extrinsicArgs = DotTxType.valueOfByTypeCode(txType).parseArgs(extrinsic);
                        BigDecimal amount = DataUtil.formatAmount(extrinsicArgs.getAmount(), CoinEnum.dot.decimals());
                        tx = new Tx();
                        tx.setHash(extrinsic.getHash().toHex());
                        tx.setBlockHash(block.getHash());
                        tx.setBlockHeight(block.getHeight());
                        tx.setFromAddr(extrinsic.getSignature().getSigner().toString());
                        tx.setToAddr(extrinsicArgs.getToAddr());
                        tx.setTimestamp(block.getTimestamp());
                        tx.setAmount(amount);
                        tx.setAssetShortName(CoinEnum.dot.name());
                        tx.setMainCoin(blockchainConfig.getMainAsset());
                        tx.setFee(fee);
                        tx.setGasPrice(BigDecimal.ZERO);
                        tx.setGasLimit(BigDecimal.ZERO);
                        tx.setTxNum(0);
                        // 自定义交易id，一般没有子交易就tx hash就可以，如果有子交易，则带上一个子交易的id，txHash_id
                        tx.setTxId(tx.getHash() + "_0");
                        tx.setState(Objects.nonNull(stateMap.get(txInfo.getIndex())) ? stateMap.get(txInfo.getIndex()) : 2);
                        tx.setType(DotTxType.valueOfByTypeCode(txType).name());
                        tx.setStatus(1);
                        tx.setBlockState((short) 1);
                        tx.setComment(extrinsicArgs.getComment());
                        tx.setFeeType(FeeType.NO_RECORD_FEE);
                        tx.setNonce(extrinsic.getSignature().getNonce().toBn().intValue());
                        this.txList.add(tx);
                    } else {
                        tx = new Tx();
                        Address address = extrinsic.getSignature().getSigner();
                        tx.setFromAddr(address.toString());
                        tx.setToAddr(address.toString());
                        // 只更新地址余额，不更新交易入库
                        tx.setSaveTx(false);
                    }
                } catch (Exception e) {
                    syncErrorService.saveError(blockchainConfig.getDataBase(), block, tx, e);
                    logger.error("tx:{} handler failed, exception: {}", txInfo.getTx(), e.getLocalizedMessage());
                } finally {
                    this.countDownLatch.countDown();
                    tx = null;
                }
            }
        };
    }
}

class TxInfo {
    private Integer index;
    private String tx;

    public TxInfo(Integer index, String tx) {
        this.index = index;
        this.tx = tx;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }
}
