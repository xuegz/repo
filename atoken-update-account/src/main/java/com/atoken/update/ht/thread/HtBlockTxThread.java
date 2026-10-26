package com.atoken.update.ht.thread;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.BatchResponse;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;
import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.Final;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.entity.coin.account.ht.HtToken;
import com.atoken.update.entity.coin.account.ht.HtTokenTx;
import com.atoken.update.entity.coin.account.ht.HtTx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.ht.constant.HtConstant;
import com.atoken.update.ht.util.HtUtil;
import com.atoken.update.service.coin.account.AccountSyncErrorService;
import com.atoken.update.service.coin.account.ht.HtTokenService;
import com.atoken.update.thread.coin.account.BaseThread;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class HtBlockTxThread extends BaseThread<List<TransactionObject>, Tx> {

    @Autowired
    @Qualifier("htWeb3j")
    private Web3j web3j;

    @Autowired
    private AccountSyncErrorService syncErrorService;

    @Autowired
    private HtTokenService htTokenService;

    @Autowired
    public HtBlockTxThread(@Qualifier("htBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }

    @Override
    public void execute() {

        try {
            // 从链上获取区块信息
            EthBlock ethBlock = this.web3j.ethGetBlockByHash(this.block.getHash(), true).send();
            List<TransactionObject> txList = Optional.ofNullable(ethBlock).map(EthBlock::getBlock)
                    .map(EthBlock.Block::getTransactions).filter(Objects::nonNull)
                    .map(txs -> txs.stream().map(TransactionObject.class::cast)
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
            // 分片处理交易数据
            int size = 20;
            List<List<TransactionObject>> partitions = Lists.partition(txList, size);
            this.countDownLatch = new CountDownLatch(partitions.size());
            txQueue.addAll(partitions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request<?, EthCall> buidEthCallTransactionRequest(long id, String from, String to,
            String data) {
        Request<?, EthCall> request =
                this.web3j.ethCall(Transaction.createEthCallTransaction(from, to, data),
                        DefaultBlockParameterName.LATEST);
        request.setId(id);
        return request;
    }

    private String getResult(Response<?> response) {
        if (response instanceof EthCall) {
            return EthCall.class.cast(response).getResult();
        }
        return null;
    }

    private HtToken getTokenFromNode(String contractAddr) {
        logger.info("getTokenFromNode contractAddr: {}", contractAddr);
        BatchRequest batchRequest = this.web3j.newBatch();
        batchRequest.add(this.buidEthCallTransactionRequest(1, null, contractAddr,
                HtConstant.FUNC_NAME_HASH));
        batchRequest.add(this.buidEthCallTransactionRequest(2, null, contractAddr,
                HtConstant.FUNC_SYMBOL_HASH));
        batchRequest.add(this.buidEthCallTransactionRequest(3, null, contractAddr,
                HtConstant.FUNC_DECIMALS_HASH));
        try {
            return Optional.ofNullable(batchRequest.sendAsync().get())
                    .map(BatchResponse::getResponses).map(Collection::stream).map(stream -> {
                        HtToken token = new HtToken();
                        // default decimals
                        token.setDecimals(0);
                        token.setContractAddr(contractAddr);
                        stream.forEach(e -> {
                            String rawResult = this.getResult(e);
                            if (StringUtils.isBlank(rawResult)) {
                                return;
                            }
                            switch ((int) e.getId()) {
                                case 1:
                                    logger.info("getTokenFromNode contractAddr: {}, raw name: {}",
                                            contractAddr, rawResult);
                                    token.setCoinFullName(HtUtil.decodeString(rawResult));
                                    break;
                                case 2:
                                    logger.info("getTokenFromNode contractAddr: {}, raw symbol: {}",
                                            contractAddr, rawResult);
                                    token.setCoinName(HtUtil.decodeString(rawResult));
                                    break;
                                case 3:
                                    logger.info(
                                            "getTokenFromNode contractAddr: {}, raw decimals: {}",
                                            contractAddr, rawResult);
                                    token.setDecimals(HtUtil.decodeUint256(rawResult).intValue());
                                    break;
                                default:;
                            }
                        });
                        logger.info("getTokenFromNode contractAddr: {}, token: {}", contractAddr,
                                JSON.toJSONString(token));
                        if (Objects.isNull(token.getCoinFullName())
                                || Objects.isNull(token.getCoinName())) {
                            return null;
                        }
                        token.setLogoUrl("icon_ht_DEFAULT.png");
                        return token;
                    }).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询token信息，如果本地查询不到，需要到节点获取并保存到本地
     * 
     * @param contractAddr
     * @return
     */
    private HtToken getToken(String contractAddr) {
        HtToken token = htTokenService.getOneByContractAddr(contractAddr);
        if (Objects.isNull(token)) {
            token = this.getTokenFromNode(contractAddr);
            htTokenService.saveOrUpdate(token);
        }
        return token;
    }

    private BigDecimal calculateFee(BigInteger gasPrice, BigInteger gasUsed) {
        return HtUtil.fromWei(gasPrice.multiply(gasUsed), Unit.ETHER);
    }

    private HtTx buildTx(TransactionObject txo, TransactionReceipt receipt) {
        HtTx tx = new HtTx();
        // tx.setHash(txo.getHash());
        tx.setTxHash(txo.getHash());
        tx.setBlockHash(block.getHash());
        tx.setBlockHeight(block.getHeight());
        tx.setFromAddr(txo.getFrom());
        tx.setToAddr(txo.getTo());
        tx.setTimestamp(block.getTimestamp());
        tx.setAssetShortName(CoinEnum.ht.name().toUpperCase());
        tx.setTxNum(0);
        tx.setTxId(txo.getHash());
        tx.setState(Numeric.decodeQuantity(receipt.getStatus()).intValue());
        tx.setStatus(1);
        tx.setBlockState((short) 1);
        tx.setFeeType(FeeType.RECORD_FEE);
        // tx.setGasLimit(new BigDecimal(txo.getGas()));
        tx.setGas(txo.getGas());
        tx.setGasPrice(HtUtil.fromWei(txo.getGasPrice(), Unit.GWEI));
        // tx.setUsedGasLimit(new BigDecimal(receipt.getGasUsed()));
        tx.setGasUsed(receipt.getGasUsed());
        tx.setFee(this.calculateFee(txo.getGasPrice(), receipt.getGasUsed()));
        tx.setNonce(txo.getNonce().intValue());
        tx.setMainCoin(CoinEnum.ht.name());
        tx.setAmount(HtUtil.fromWei(txo.getValue(), Unit.ETHER));
        return tx;
    }

    private HtTokenTx buildBaseTokenTx(HtTx source) {
        HtTokenTx tx = new HtTokenTx();
//        tx.setContractAddr(source.getToAddr());
        // tx.setHash(source.getHash());
        tx.setTxHash(source.getTxHash());
        tx.setBlockHash(source.getBlockHash());
        tx.setBlockHeight(source.getBlockHeight());
        tx.setTimestamp(source.getTimestamp());
        tx.setTxId(source.getHash());
        tx.setState(source.getState());
        tx.setStatus(source.getStatus());
        tx.setBlockState(source.getBlockState());
        tx.setFeeType(source.getFeeType());
        // tx.setGasLimit(source.getGasLimit());
        tx.setGas(source.getGas());
        tx.setGasPrice(source.getGasPrice());
        // tx.setUsedGasLimit(source.getUsedGasLimit());
        tx.setGasUsed(source.getGasUsed());
        tx.setFee(source.getFee());
        tx.setNonce(source.getNonce());
        tx.setMainCoin(CoinEnum.ht.name());
        return tx;
    }

    private boolean validTopics(List<String> topics) {
        return CollectionUtils.size(topics) >= 3
                && StringUtils.equals(HtConstant.TRANSFER_TOPIC, topics.get(0));
    }

    private boolean decodeLogAndSetValues(final HtTokenTx tokenTx, Log log) {
        return Optional.of(log).filter(e -> !e.isRemoved() && this.validTopics(e.getTopics()))
                .map(e -> {
                    String contractAddr = e.getAddress();
                    // 获取token
                    HtToken token = this.getToken(contractAddr);
                    if (Objects.isNull(token)) {
                        return false;
                    }
                    int decimals = ObjectUtils.defaultIfNull(token.getDecimals(), 0);
                    List<String> topics = log.getTopics();
                    String data = log.getData();
                    int size = CollectionUtils.size(topics);
                    tokenTx.setContractAddr(contractAddr);
                    tokenTx.setFromAddr(HtUtil.decodeAddress(topics.get(1)));
                    tokenTx.setToAddr(HtUtil.decodeAddress(topics.get(2)));
                    tokenTx.setAmount(HtUtil.fromWei(
                            HtUtil.decodeUint256(StringUtils
                                    .defaultIfBlank(size > 3 ? topics.get(3) : data, "0")),
                            decimals));
                    tokenTx.setDecimals(decimals);
                    tokenTx.setTxNum(log.getLogIndex().intValue());
                    return true;
                }).orElse(false);
    }

    private List<HtTokenTx> buildTokenTxs(HtTx source, List<Log> logs) {
        return Optional.ofNullable(logs).map(Collection::stream).map(stream -> stream.map(log -> {
            HtTokenTx tokenTx = this.buildBaseTokenTx(source);
            boolean result = this.decodeLogAndSetValues(tokenTx, log);
            return result ? tokenTx : null;
        }).filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public Runnable runnable() {
        return () -> {
            while (true) {
                Final<Tx> globalTx = Final.newFinal(null);
                try {
                    // 获取交易hash
                    List<TransactionObject> txoList = txQueue.take();
                    if (CollectionUtils.isEmpty(txoList)) {
                        continue;
                    }
                    logger.info("ht txs:{} heandler start", txoList.size());
                    BatchRequest batchRequest = this.web3j.newBatch();
                    txoList.stream().map(TransactionObject::getHash)
                            .map(this.web3j::ethGetTransactionReceipt).forEach(batchRequest::add);
                    BatchResponse batchResponse = batchRequest.send();

                    // 批量获取交易凭证
                    Map<String, TransactionReceipt> receiptMap = Maps.newConcurrentMap();

                    Optional.ofNullable(batchResponse).map(BatchResponse::getResponses)
                            .filter(Objects::nonNull).map(Collection::stream)
                            .ifPresent(stream -> stream
                                    .filter(e -> e instanceof EthGetTransactionReceipt)
                                    .map(EthGetTransactionReceipt.class::cast)
                                    .map(EthGetTransactionReceipt::getResult)
                                    .forEach(e -> receiptMap.put(e.getTransactionHash(), e)));

                    txoList.forEach(txo -> {
                        String txHash = txo.getHash();
                        TransactionReceipt receipt = receiptMap.get(txHash);
                        Optional.ofNullable(receipt).ifPresent(e -> {
                            // 生成普通交易对象
                            HtTx tx = this.buildTx(txo, e);
                            globalTx.setType(tx);
                            // 生成合约交易对象
                            List<HtTokenTx> tokenTxs = this.buildTokenTxs(tx, e.getLogs());
                            this.txList.add(tx);
                            this.txList.addAll(tokenTxs);
                        });

                    });
                } catch (Exception e) {
                    syncErrorService.saveError(blockchainConfig.getTablePrefix(), block,
                            globalTx.getType(), e);
                    logger.error("tx:{} handler failed, exception: {}",
                            Optional.ofNullable(globalTx.getType()).map(Tx::getHash).orElse(null),
                            e.getLocalizedMessage());
                } finally {
                    this.countDownLatch.countDown();
                }
            }
        };
    }
}
