package com.atoken.update.ont.thread;

import com.atoken.blockchain.http.request.account.ont.*;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.ont.Token;
import com.atoken.update.entity.ont.Tx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.service.ont.OntSyncErrorService;
import com.atoken.update.thread.ont.BaseThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Component
public class OntBlockTxThread extends BaseThread<OntTransaction> {
    private OntBlock ontBlock;
    @Autowired
    private OntClient ontClient;
    private Map<String, OntSmartCodeEvent> eventMap;
    @Autowired
    @Qualifier("tokenMap")
    private Map<String, Token> tokenMap;
    @Autowired
    private OntSyncErrorService ontSyncErrorService;

    @Override
    public void execute() {
        // 从链上获取区块信息
        this.ontBlock = ontClient.getBlockByHeight(this.block.getHeight());

        this.countDownLatch = new CountDownLatch(this.ontBlock.getTransactions().size());

        // 通过高度，获取事件信息，后面交易解析1时候需要用到这个事件
        this.eventMap = new HashMap<>();
        List<OntSmartCodeEvent> ontSmartCodeEvents = ontClient.getSmartCodeEvent(block.getHeight());
        if (Objects.nonNull(ontSmartCodeEvents)) {
            ontSmartCodeEvents.forEach(ontSmartCodeEvent -> eventMap.put(ontSmartCodeEvent.getTxHash(), ontSmartCodeEvent));
        }

        // 将交易塞入队列等待处理
        this.ontBlock.getTransactions().forEach(tx -> {
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
            OntTransaction transaction = null;
            Tx tx = null;
            while(true) {
                try {
                    // 获取队列里1交易
                    transaction = txQueue.take();
                    logger.info("tx:{} heandler start", transaction.getHash());

                    // 获取事件信息
                    OntSmartCodeEvent ontSmartCodeEvent = eventMap.get(transaction.getHash());

                    // 解析事件
                    for (int i = 0, size = ontSmartCodeEvent.getNotify().size(); i < size; i++) {
                        // 获取事件的notify
                        OntNotify ontNotify = ontSmartCodeEvent.getNotify().get(i);
                        // 获取token信息
                        Token token = tokenMap.get(ontNotify.getContractAddress());

                        // 查看是否是我们支持的token，并且是交易类型
                        if (Objects.nonNull(token) && OntMethod.transfer.equals(ontNotify.getTransfer().getMethod())) {
                            tx = new Tx();
                            tx.setHash(transaction.getHash());
                            tx.setBlockHash(ontBlock.getHash());
                            tx.setBlockHeight(ontBlock.getHeader().getHeight());
                            tx.setFromAddr(ontNotify.getTransfer().getFrom());
                            tx.setToAddr(ontNotify.getTransfer().getTo());
                            tx.setTimestamp(ontBlock.getHeader().getTimestamp());
                            tx.setAmount(DataUtil.formatAmount(ontNotify.getTransfer().getAmount(), token.getDecimals()));
                            tx.setAssetShortName(token.getName());
                            tx.setContractAddr(ontNotify.getContractAddress());
                            tx.setFee(DataUtil.formatAmount(ontSmartCodeEvent.getGasConsumed(), OntAssetCode.ONG.decimals()));
                            tx.setGasLimit(transaction.getGasLimit());
                            tx.setGasPrice(DataUtil.formatAmount(transaction.getGasPrice(), OntAssetCode.ONG.decimals()));
                            tx.setTxNum(i);
                            tx.setTxId(tx.getHash() + "_" + tx.getTxNum());
                            tx.setState(ontSmartCodeEvent.getState());
                            tx.setType(ontNotify.getTransfer().getMethod().name());
                            tx.setStatus(1);
                            tx.setBlockState((short)1);
                            tx.setFeeType(FeeType.NO_RECORD_FEE);
                            this.txList.add(tx);
                        }
                    }
                } catch (Exception e) {
                    ontSyncErrorService.saveError(block, tx, e);
                    logger.error("tx:{} handler failed, exception: {}", transaction.getHash(), e.getLocalizedMessage());
                } finally {
                    this.countDownLatch.countDown();
                }
            }
        };
    }
}
