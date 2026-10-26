package com.atoken.update.tomo.thread;

import com.atoken.blockchain.http.request.account.tomo.ContractEnum;
import com.atoken.blockchain.http.request.account.tomo.ContractFunctionEnum;
import com.atoken.blockchain.http.request.account.tomo.TomoClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.service.coin.account.AccountSyncErrorService;
import com.atoken.update.thread.coin.account.BaseThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Component
public class TomoBlockTxThread extends BaseThread<EthBlock.TransactionObject, Tx> {
    @Autowired
    private TomoClient tomoClient;
    @Autowired
    private AccountSyncErrorService syncErrorService;

    @Autowired
    public TomoBlockTxThread(@Qualifier("tomoBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }

    @Override
    public void execute() {
        EthBlock.Block tomoBlock = tomoClient.getBlockByHeight(block.getHeight());
        List<EthBlock.TransactionResult> txList = tomoBlock.getTransactions();
        this.countDownLatch = new CountDownLatch(txList.size());
        txList.forEach(tx -> {
            try {
                txQueue.put((EthBlock.TransactionObject) tx);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Runnable runnable() {
        return () -> {
            EthBlock.TransactionObject tomoTx = null;
            Tx tx = null;
            while (true) {
                try {
                    tomoTx = txQueue.take();
                    logger.info("tomo tx:{} heandler start", tomoTx.getHash());
                    if (isHandler(tomoTx)) {
                        // 获取交易信息
                        TransactionReceipt transactionReceipt = tomoClient.getTransactionReceiptByTxHash(tomoTx.getHash());
                        tx = new Tx();
                        tx.setHash(tomoTx.getHash());
                        tx.setBlockHash(block.getHash());
                        tx.setBlockHeight(block.getHeight());
                        tx.setFromAddr(tomoTx.getFrom());
                        tx.setTimestamp(block.getTimestamp());
                        tx.setAssetShortName(CoinEnum.tomo.name());
                        tx.setMainCoin(blockchainConfig.getMainAsset());
                        tx.setUsedGasLimit(new BigDecimal(transactionReceipt.getGasUsed()));
                        tx.setGasPrice(new BigDecimal(tomoTx.getGasPrice()));
                        tx.setGasLimit(new BigDecimal(tomoTx.getGas()));
                        tx.setTxNum(0);
                        tx.setTxId(tx.getHash() + "_" + 0);
                        tx.setStatus(1);
                        tx.setBlockState((short) 1);
                        tx.setNonce(tomoTx.getNonce().intValue());
                        tx.setFeeType(FeeType.RECORD_FEE);
                        tx.setFee(DataUtil.formatAmount(new BigDecimal(tomoTx.getGasPrice().multiply(transactionReceipt.getGasUsed())), CoinEnum.tomo.decimals()));
                        if (ContractFunctionEnum.isNormalTx(tomoTx.getInput())) {
                            // 普通交易
                            tx.setToAddr(tomoTx.getTo());
                            tx.setAmount(DataUtil.formatAmount(new BigDecimal(tomoTx.getValue()), CoinEnum.tomo.decimals()));
                            tx.setState(1);
                            tx.setType(ContractFunctionEnum.NORMAL.name());
                        } else if (ContractEnum.isStaking(tomoTx.getTo())) {
                            // 质押交易
                            // 解析质押交易信息
                            ContractFunctionEnum.ContractInfo contractInfo = ContractFunctionEnum.parse(tomoTx.getInput(), transactionReceipt.getLogs().get(0).getData());
                            tx.setToAddr(Objects.isNull(contractInfo.getAddress()) ? tomoTx.getTo(): contractInfo.getAddress());
                            tx.setAmount(Objects.isNull(contractInfo.getAmount()) ? DataUtil.formatAmount(new BigDecimal(tomoTx.getValue()), CoinEnum.tomo.decimals()): contractInfo.getAmount());
                            tx.setState(transactionReceipt.isStatusOK() ? 1: 0);
                            tx.setType(contractInfo.getContractFunctionEnum().name());
                            tx.setSaveTx(true);
                            tx.setSendMessage(false);
                        }
                        this.txList.add(tx);
                    }
                } catch (Exception e) {
                    syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, tx, e);
                    logger.error("tx:{} handler failed, exception: {}", tomoTx.getHash(), e.getLocalizedMessage());
                } finally {
                    this.countDownLatch.countDown();
                    tomoTx = null;
                }
            }
        };
    }

    private boolean isHandler(EthBlock.TransactionObject tomoTx) {
        return ContractFunctionEnum.isNormalTx(tomoTx.getInput()) || ContractEnum.isStaking(tomoTx.getTo());
    }
}
