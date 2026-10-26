package com.atoken.update.bnb.thread;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.update.bnb.constant.BnbConstant;
import com.atoken.update.bnb.util.BnbUtil;
import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.Final;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.entity.coin.account.bnb.BnbToken;
import com.atoken.update.entity.coin.account.bnb.BnbTokenTx;
import com.atoken.update.entity.coin.account.bnb.BnbTx;
import com.atoken.update.enums.FeeType;
import com.atoken.update.service.coin.account.AccountAddressService;
import com.atoken.update.service.coin.account.AccountSyncErrorService;
import com.atoken.update.service.coin.account.bnb.BnbTokenService;
import com.atoken.update.thread.coin.account.BaseThread;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.*;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class BnbBlockTxThread extends BaseThread<List<TransactionObject>, Tx> {

    @Autowired
    @Qualifier("bnbWeb3j")
    private Web3j web3j;

    @Autowired
    private AccountSyncErrorService syncErrorService;

    @Autowired
    private BnbTokenService bnbTokenService;

    @Autowired
    private AccountAddressService addressService;

    @Autowired
    public BnbBlockTxThread(@Qualifier("bnbBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockchainConfig);
    }

    @Override
    public void execute() {
        try {
            // 从链上获取区块信息
            EthBlock ethBlock = this.web3j.ethGetBlockByHash(this.block.getHash(), true).send();
            List<TransactionObject> txList = Optional.ofNullable(ethBlock)
                    .map(EthBlock::getBlock).map(EthBlock.Block::getTransactions)
                    .map(txs -> txs.stream().map(TransactionObject.class::cast).collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
            // 分片处理交易数据
            int size = 20;
            List<List<TransactionObject>> partitions = Lists.partition(txList, size);
            this.countDownLatch = new CountDownLatch(partitions.size());
            txQueue.addAll(partitions);
        } catch (Exception e) {
            logger.error("execute error", e);
        }
    }

    private Request<?, EthCall> buidEthCallTransactionRequest(long id, String from, String to, String data) {
        Request<?, EthCall> request = this.web3j.ethCall(Transaction.createEthCallTransaction(from, to, data),
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

    private BnbToken getTokenFromNode(String contractAddr) {
        logger.info("getTokenFromNode contractAddr: {}", contractAddr);
        BatchRequest batchRequest = this.web3j.newBatch();
        batchRequest.add(this.buidEthCallTransactionRequest(1, null, contractAddr,
                BnbConstant.FUNC_NAME_HASH));
        batchRequest.add(this.buidEthCallTransactionRequest(2, null, contractAddr,
                BnbConstant.FUNC_SYMBOL_HASH));
        batchRequest.add(this.buidEthCallTransactionRequest(3, null, contractAddr,
                BnbConstant.FUNC_DECIMALS_HASH));
        try {
            return Optional.ofNullable(batchRequest.sendAsync().get())
                    .map(BatchResponse::getResponses).map(Collection::stream).map(stream -> {
                        BnbToken token = new BnbToken();
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
                                    token.setCoinFullName(BnbUtil.decodeString(rawResult));
                                    break;
                                case 2:
                                    logger.info("getTokenFromNode contractAddr: {}, raw symbol: {}",
                                            contractAddr, rawResult);
                                    token.setCoinName(BnbUtil.decodeString(rawResult));
                                    break;
                                case 3:
                                    logger.info(
                                            "getTokenFromNode contractAddr: {}, raw decimals: {}",
                                            contractAddr, rawResult);
                                    token.setDecimals(BnbUtil.decodeUint256(rawResult).intValue());
                                    break;
                                default:
                                    ;
                            }
                        });
                        logger.info("getTokenFromNode contractAddr: {}, token: {}", contractAddr,
                                JSON.toJSONString(token));
                        if (Objects.isNull(token.getCoinFullName())
                                || Objects.isNull(token.getCoinName())) {
                            return null;
                        }
                        token.setLogoUrl("icon_bnb_DEFAULT.png");
                        return token;
                    }).orElse(null);
        } catch (Exception e) {
            logger.error("BNB getTokenFromNode error, contractAddr:{}", contractAddr, e);
        }
        return null;
    }

    /**
     * 查询token信息，如果本地查询不到，需要到节点获取并保存到本地
     *
     * @param contractAddr
     * @return
     */
    private BnbToken getToken(String contractAddr) {
        BnbToken token = bnbTokenService.getOneByContractAddr(contractAddr);
        if (Objects.isNull(token)) {
            token = this.getTokenFromNode(contractAddr);
            bnbTokenService.saveOrUpdate(token);
        }
        return token;
    }

    private BigDecimal calculateFee(BigInteger gasPrice, BigInteger gasUsed) {
        return BnbUtil.fromWei(gasPrice.multiply(gasUsed), Unit.ETHER);
    }

    private BnbTx buildTx(TransactionObject txo, TransactionReceipt receipt) {
        BnbTx tx = new BnbTx();
        tx.setTxHash(txo.getHash());
        tx.setBlockHash(block.getHash());
        tx.setBlockHeight(block.getHeight());
        tx.setFromAddr(txo.getFrom());
        tx.setToAddr(txo.getTo());
        tx.setTimestamp(block.getTimestamp());
        tx.setAssetShortName(CoinEnum.bnb.name().toUpperCase());
        tx.setTxNum(0);
        tx.setTxId(txo.getHash());
        tx.setState(Numeric.decodeQuantity(receipt.getStatus()).intValue());
        tx.setStatus(1);
        tx.setBlockState((short) 1);
        tx.setFeeType(FeeType.RECORD_FEE);
        tx.setGas(txo.getGas());
        tx.setGasPrice(BnbUtil.fromWei(txo.getGasPrice(), Unit.GWEI));
        tx.setGasUsed(receipt.getGasUsed());
        tx.setFee(this.calculateFee(txo.getGasPrice(), receipt.getGasUsed()));
        tx.setNonce(txo.getNonce().intValue());
        tx.setMainCoin(CoinEnum.bnb.name());
        tx.setAmount(BnbUtil.fromWei(txo.getValue(), Unit.ETHER));
        return tx;
    }

    private BnbTokenTx buildBaseTokenTx(BnbTx source) {
        BnbTokenTx tx = new BnbTokenTx();
//        tx.setContractAddr(source.getToAddr());
        tx.setTxHash(source.getTxHash());
        tx.setBlockHash(source.getBlockHash());
        tx.setBlockHeight(source.getBlockHeight());
        tx.setTimestamp(source.getTimestamp());
        tx.setTxId(source.getHash());
        tx.setState(source.getState());
        tx.setStatus(source.getStatus());
        tx.setBlockState(source.getBlockState());
        tx.setFeeType(source.getFeeType());
        tx.setGas(source.getGas());
        tx.setGasPrice(source.getGasPrice());
        tx.setGasUsed(source.getGasUsed());
        tx.setFee(source.getFee());
        tx.setNonce(source.getNonce());
        tx.setMainCoin(CoinEnum.bnb.name());
        return tx;
    }

    private boolean validTopics(List<String> topics) {
        return CollectionUtils.size(topics) >= 3
                && StringUtils.equals(BnbConstant.TRANSFER_TOPIC, topics.get(0));
    }

    private boolean decodeLogAndSetValues(final BnbTokenTx tokenTx, Log log) {
        return Optional.of(log).filter(e -> !e.isRemoved() && this.validTopics(e.getTopics()))
                .map(e -> {
                    String contractAddr = e.getAddress();
                    // 获取token
                    BnbToken token = this.getToken(contractAddr);
                    if (Objects.isNull(token)) {
                        return false;
                    }
                    int decimals = ObjectUtils.defaultIfNull(token.getDecimals(), 0);
                    List<String> topics = log.getTopics();
                    String data = log.getData();
                    int size = CollectionUtils.size(topics);
                    tokenTx.setContractAddr(contractAddr);
                    tokenTx.setFromAddr(BnbUtil.decodeAddress(topics.get(1)));
                    tokenTx.setToAddr(BnbUtil.decodeAddress(topics.get(2)));
                    tokenTx.setAmount(BnbUtil.fromWei(
                            BnbUtil.decodeUint256(StringUtils
                                    .defaultIfBlank(size > 3 ? topics.get(3) : data, "0")),
                            decimals));
                    tokenTx.setDecimals(decimals);
                    tokenTx.setTxNum(log.getLogIndex().intValue());
                    return true;
                }).orElse(false);
    }

    private List<BnbTokenTx> buildTokenTxs(BnbTx source, List<Log> logs) {
        return Optional.ofNullable(logs).map(Collection::stream).map(stream -> stream.map(log -> {
            BnbTokenTx tokenTx = this.buildBaseTokenTx(source);
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
                    // check 地址是否存在于地址库
                    txoList = txoList.stream().filter(e -> {
                        String fromAddr = e.getFrom();
                        String toAddr;
                        String input = e.getInput();
                        if (StringUtils.isNotEmpty(input) && input.startsWith("0xa9059cbb")) {
                            toAddr = "0x" + input.substring(34, 74);
                        } else {
                            toAddr = e.getTo();
                        }
                        List<Address> addresses = addressService
                                .selectAddressByAddress(blockchainConfig.getTablePrefix(), fromAddr, toAddr);
                        return CollectionUtils.isNotEmpty(addresses);
                    }).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(txoList)) {
                        continue;
                    }

                    logger.info("bnb txs:{} heandler start", txoList.size());
                    BatchRequest batchRequest = this.web3j.newBatch();
                    txoList.stream().map(TransactionObject::getHash)
                            .map(this.web3j::ethGetTransactionReceipt).forEach(batchRequest::add);
                    BatchResponse batchResponse = batchRequest.send();

                    // 批量获取交易凭证
                    Map<String, TransactionReceipt> receiptMap = Maps.newConcurrentMap();

                    Optional.ofNullable(batchResponse).map(BatchResponse::getResponses).map(Collection::stream)
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
                            BnbTx tx = this.buildTx(txo, e);
                            globalTx.setType(tx);
                            // 生成合约交易对象
                            List<BnbTokenTx> tokenTxs = this.buildTokenTxs(tx, e.getLogs());
                            this.txList.add(tx);
                            this.txList.addAll(tokenTxs);
                        });
                    });
                } catch (Exception e) {
                    syncErrorService.saveError(blockchainConfig.getTablePrefix(), block, globalTx.getType(), e);
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
