package com.atoken.update.atom.thread;

import com.atoken.blockchain.http.request.account.atom.*;
import com.atoken.blockchain.http.request.enums.AtomCoin;
import com.atoken.blockchain.http.request.enums.AtomTxType;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.blockchain.utils.SecurityUtil;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.atom.AtomTx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.service.SyncErrorService;
import com.atoken.update.thread.BaseThread;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class AtomBlockTxThread extends BaseThread<String, AtomTx> {
    private AtomBlockInfo atomBlockInfo;
    @Autowired
    private AtomClient atomClient;

    @Autowired
    private SyncErrorService syncErrorService;

    @Autowired
    public AtomBlockTxThread(@Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }

    @Override
    public void execute() {

        // 从链上获取区块信息
//        this.atomBlockInfo = atomClient.getBlockByHeight(this.block.getHeight());

        this.atomBlockInfo = atomClient.getBlockByHeight(3540092);

        if (this.atomBlockInfo.getBlock().getData().getTxs() == null) {
            this.atomBlockInfo.getBlock().getData().setTxs(new ArrayList<>());
        }
        // 获取该区块的tx hash
        List<String> txList = this.atomBlockInfo.getBlock().getData().getTxs().stream().map(tx -> SecurityUtil.encoderBySha256(Base64.decode(tx))).collect(Collectors.toList());
        this.countDownLatch = new CountDownLatch(txList.size());
        txList.forEach(tx -> {
            try {
                txQueue.put(tx);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Runnable runnable() {
        return () -> {
            String txHash = null;
            AtomTx tx = null;
            while (true) {
                try {
                    // 获取交易hash
                    txHash = txQueue.take();
                    logger.info("atom tx:{} heandler start", txHash);
                    // 通过交易hash获取交易信息
                    AtomTxInfo atomTxInfo = atomClient.getTxByTxHash(txHash);
                    // 更新资产信息
                    Map<Integer, BigDecimal> rewardMap = parseRewards(atomTxInfo);
                    BigDecimal fee = BigDecimal.ZERO;
                    // 计算交易手续费
                    if (Objects.nonNull(atomTxInfo.getTx().getValue().getFee().getAmount())) {
                        fee = atomTxInfo.getTx().getValue().getFee().getAmount().stream().filter(amt -> AtomCoin.uatom.isCoin(amt.getDenom())).map(amt -> amt.getAmount()).findFirst().get();
                    }
                    fee = DataUtil.formatAmount(fee, CoinEnum.atom.decimals());
                    for (int i = 0, size = atomTxInfo.getTx().getValue().getMsg().size(); i < size; i++) {
                        // 获取交易信息
                        AtomMsg atomMsg = atomTxInfo.getTx().getValue().getMsg().get(i);
                        // 如果是我们需要处理的交易
                        if (isHandler(atomMsg)) {
                            tx = new AtomTx();

                            // 计算金额
                            BigDecimal amount = BigDecimal.ZERO;
                            if (Objects.nonNull(atomMsg.getValue().getAmount())) {
                                amount = atomMsg.getValue().getAmount().stream().filter(amt -> AtomCoin.uatom.isCoin(amt.getDenom())
                                        || AtomCoin.atom.isCoin(amt.getDenom())
                                        || AtomCoin.undefined.isCoin(amt.getDenom()))
                                        .map(amt -> amt.getAmount()).findFirst().get();
                                amount = DataUtil.formatAmount(amount, CoinEnum.atom.decimals());
                            } else if (AtomTxType.MSGWITHDRAWDELEGATIONREWARD.type().equals(atomMsg.getType())) {
                                amount = rewardMap.get(i);
                                if(Objects.isNull(amount)) {
                                    amount = BigDecimal.ZERO;
                                }
                            }
                            // 解析链上状态
                            Integer state = 0;
                            try {
                                state = atomTxInfo.getLogs().get(i).getSuccess() ? 1 : 0;
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                            String fromAddress = atomMsg.getValue().getFromAddress();
                            String toAddress = atomMsg.getValue().getToAddress();
                            tx.setHash(atomTxInfo.getTxhash());
                            tx.setBlockHash(block.getHash());
                            tx.setBlockHeight(block.getHeight());
                            tx.setFromAddr(fromAddress);
                            tx.setToAddr(toAddress);
                            tx.setTimestamp(block.getTimestamp());
                            tx.setAmount(amount);
                            tx.setAssetShortName(CoinEnum.atom.name());
                            tx.setMainCoin(blockchainConfig.getMainAsset());
                            tx.setFee(fee);
                            tx.setGasPrice(BigDecimal.ZERO);
                            tx.setGasLimit(new BigDecimal(atomTxInfo.getGasUsed()));
                            tx.setTxNum(i);
                            tx.setTxId(tx.getHash() + "_" + i);
                            tx.setState(state);
                            tx.setType(atomMsg.getType());
                            tx.setStatus(1);
                            tx.setBlockState((short) 1);
                            tx.setComment(atomTxInfo.getTx().getValue().getMemo());
                            tx.setFeeType(FeeType.NO_RECORD_FEE);
                            tx.setNonce(-1);
                            this.txList.add(tx);
                        }
                    }
                } catch (Exception e) {
                    syncErrorService.saveError(blockchainConfig.getDataBase(), block, tx, e);
                    logger.error("tx:{} handler failed, exception: {}", txHash, e.getLocalizedMessage());
                } finally {
                    this.countDownLatch.countDown();
                    tx = null;
                }
            }
        };
    }

    /**
     * 解析收益金额
     * @param atomTxInfo
     * @return
     */
    private Map<Integer, BigDecimal> parseRewards(AtomTxInfo atomTxInfo) {
//        int index = 0;
        Map<Integer, BigDecimal> rewardMap = new HashMap<>();
        try {
            for (int i = 0, len = atomTxInfo.getLogs().size(); i < len; i++) {
                // 获取log信息
                AtomLog atomLog = atomTxInfo.getLogs().get(i);
                atomLog.getEvents().forEach(atomEvent -> {
                    // 根据交易类型查询出金额
                    if("withdraw_reward".equals(atomEvent.getType())) {
                        atomEvent.getAttributes().forEach(atomTag -> {
                            if("amount".equals(atomTag.getKey())) {
                                rewardMap.put(atomLog.getMsgIndex(), DataUtil.formatAmount(new BigDecimal(atomTag.getValue().replace("uatom", "")), CoinEnum.atom.decimals()));
                            }
                        });
                    }
                });
//                if ("withdraw_delegator_reward".equals(atomTag.getValue())) {
//                    atomTag = atomTxInfo.getTags().get(++i);
//                    rewardMap.put(index++, DataUtil.formatAmount(new BigDecimal(atomTag.getValue().replace("uatom", "")), CoinEnum.atom.decimals()));
//                    i += 2;
//                } else if ("action".equals(atomTag.getKey())) {
//                    index++;
//                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
        return rewardMap;
    }

    private boolean isHandler(AtomMsg atomMsg) {
        AtomTxType atomTxType = AtomTxType.atomTxType(atomMsg.getType());
        boolean isHandler = false;
        switch (atomTxType) {
            case MSGSEND:
            case MSGDELEGATE:
            case MSGUNDELEGATE:
            case MSGWITHDRAWDELEGATIONREWARD:
                isHandler = true;
        }
        return isHandler;
    }
}
