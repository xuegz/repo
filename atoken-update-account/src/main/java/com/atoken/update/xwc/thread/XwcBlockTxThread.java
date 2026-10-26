package com.atoken.update.xwc.thread;

import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.account.xwc.XwcClient;
import com.atoken.blockchain.http.request.account.xwc.response.XwcBlockResponse;
import com.atoken.blockchain.http.request.account.xwc.response.XwcOperations;
import com.atoken.blockchain.http.request.account.xwc.response.XwcTransactionResponse;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.service.coin.account.AccountSyncErrorService;
import com.atoken.update.thread.coin.account.BaseThread;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Component
public class XwcBlockTxThread extends BaseThread<String, Tx> {
    private XwcBlockResponse xwcBlockResponse;
    @Autowired
    private XwcClient xwcClient;

    @Autowired
    private AccountSyncErrorService syncErrorService;

    @Autowired
    public XwcBlockTxThread(@Qualifier("xwcBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }

    @Override
    public void execute() {

        // 从链上获取区块信息
        this.xwcBlockResponse = xwcClient.getBlock(this.block.getHeight());
        if (CollectionUtils.isEmpty(this.xwcBlockResponse.getTransactions())) {
            this.xwcBlockResponse.setTransactions(Lists.newArrayList());
        }
        // 获取该区块的tx hash
        List<String> txList = this.xwcBlockResponse.getTransactionIds();
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
            Tx tx = null;
            while (true) {
                try {
                    // 获取交易hash
                    txHash = txQueue.take();
                    logger.info("xwc tx:{} heandler start", txHash);
                    // 通过交易hash获取交易信息
                    XwcTransactionResponse xwcTransactionResponse = xwcClient.getTransaction(txHash);
                    List<Object> objects = xwcTransactionResponse.getOperations().stream().filter(Objects::nonNull).findFirst().get();
                    if(Integer.parseInt(objects.stream().findFirst().get().toString())==0){
                    XwcOperations xwcOperations = JSONObject.toJavaObject(JSONObject.parseObject(objects.get(1).toString()), XwcOperations.class);
                        // 获取交易信息
                        tx = new Tx();
                        tx.setHash(xwcTransactionResponse.getTrxid());
                        tx.setBlockHash(block.getHash());
                        tx.setBlockHeight(block.getHeight());
                        tx.setFromAddr(xwcOperations.getFromAddr());
                        tx.setToAddr(xwcOperations.getToAddr());
                        tx.setTimestamp(block.getTimestamp());
                        tx.setAssetShortName(CoinEnum.xwc.name());
                        tx.setMainCoin(blockchainConfig.getMainAsset());
                        tx.setTxNum(0);
                        tx.setTxId(tx.getHash() + "_" + 0);
                        tx.setState(1);
                        tx.setStatus(1);
                        tx.setBlockState((short) 1);
                        tx.setFeeType(xwcOperations.getFee().getAssetId()
                                .equals(xwcOperations.getAmount().getAssetId()) ? FeeType.RECORD_FEE : FeeType.NO_RECORD_FEE);
                        tx.setFee(DataUtil.formatAmount(xwcOperations.getFee().getAmount(),8));
                        tx.setMainCoin(CoinEnum.xwc.name());
                        tx.setAmount(DataUtil.formatAmount(xwcOperations.getAmount().getAmount(),8));
                        this.txList.add(tx);
                    }
                } catch (Exception e) {
                    syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, tx, e);
                    logger.error("tx:{} handler failed, exception: {}", txHash, e.getLocalizedMessage());
                } finally {
                    this.countDownLatch.countDown();
                    tx = null;
                }
            }
        };
    }
}
