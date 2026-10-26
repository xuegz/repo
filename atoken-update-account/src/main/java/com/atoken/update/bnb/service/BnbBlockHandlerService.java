package com.atoken.update.bnb.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.atoken.update.entity.TxRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.BatchResponse;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;
import com.atoken.update.bnb.constant.BnbConstant;
import com.atoken.update.bnb.thread.BnbBlockTxThread;
import com.atoken.update.bnb.util.BnbUtil;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.exception.NodeException;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.util.AtokenLogger;
import com.google.common.collect.Lists;

/**
 * @author atoken-team
 * @date 2021/02/23
 */
@Service("bnbBlockHandlerService")
public class BnbBlockHandlerService implements BlockHandlerService, AtokenLogger {

    @Autowired
    @Qualifier("bnbWeb3j")
    private Web3j web3j;

    @Autowired
    private BnbBlockTxThread bnbBlockTxThread;

    @Override
    public Integer getBlockNumber() {
        try {
            return Optional.ofNullable(this.web3j.ethBlockNumber().send())
                    .map(EthBlockNumber::getBlockNumber).map(BigInteger::intValue).orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Block> getBlocks(List<Integer> blockHeights) {

        return Optional.ofNullable(blockHeights).filter(CollectionUtils::isNotEmpty).map(list -> {
            BatchRequest batchRequest = this.web3j.newBatch();
            list.forEach(e -> batchRequest.add(this.web3j.ethGetBlockByNumber(
                    DefaultBlockParameter.valueOf(BigInteger.valueOf(e)), false)));
            List<Block> blockList = Lists.newArrayListWithCapacity(blockHeights.size());
            try {
                Optional.ofNullable(batchRequest.send()).map(BatchResponse::getResponses)
                        .map(Collection::stream)
                        .ifPresent(stream -> blockList.addAll(
                                stream.filter(e -> e instanceof EthBlock).map(EthBlock.class::cast)
                                        .map(EthBlock::getBlock).map(e -> {

                                            // 获取区块信息
                                            Block block = new Block();
                                            block.setHash(e.getHash());
                                            block.setHeight(e.getNumber().intValue());
                                            block.setPrevious(e.getParentHash());
                                            block.setSize(e.getSize().intValue());
                                            block.setTimestamp(
                                                    (e.getTimestamp().longValue()));
                                            block.setTxCount(0);
                                            block.setState((short) 1);
                                            logger.info("block: {} handler end", block.getHeight());
                                            return block;
                                        }).collect(Collectors.toList())));
            } catch (IOException e) {
                if (CollectionUtils.isEmpty(blockHeights)) {
                    logger.info("blockHeights is empty");
                } else {
                    logger.info("block: {} - {} handler faided", blockList.get(0),
                            blockList.get(blockList.size() - 1), e);
                }

            }
            return blockList;
        }).orElse(Collections.emptyList());
    }

    @Override
    public List<Tx> getBlockTx(Block block) {
        try {
            bnbBlockTxThread.init(block);
            bnbBlockTxThread.execute();
            bnbBlockTxThread.await();
            return bnbBlockTxThread.getTxList();
        } catch (Exception e) {
            throw new NodeException(e);
        }
    }

    @Override
    public String getBlockHash(Integer blockHeight) {
        try {
            return Optional
                    .ofNullable(
                            this.web3j
                                    .ethGetBlockByNumber(DefaultBlockParameter
                                            .valueOf(BigInteger.valueOf(blockHeight)), false)
                                    .send())
                    .map(EthBlock::getBlock).map(EthBlock.Block::getHash).orElse(null);
        } catch (IOException e) {
            logger.info("block: {} handler faided", blockHeight, e);
        }
        return null;
    }

    @Override
    public Integer getMemPoolTxCount() {
        // TODO 本次接币不实现
        return null;
    }

    @Override
    public BigDecimal getAddressBalance(String address) {
        try {
            return Optional
                    .ofNullable(this.web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                            .send())
                    .map(EthGetBalance::getBalance).map(e -> BnbUtil.fromWei(e, Unit.ETHER))
                    .orElse(BigDecimal.ZERO);
        } catch (IOException e) {
            logger.info("address: {} handler faided", address, e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTokenBalance(String contractAddr, String address, Integer decimals) {
        try {
            return Optional
                    .ofNullable(this.web3j.ethCall(
                            Transaction.createEthCallTransaction(address, contractAddr,
                                    BnbConstant.FUNC_BALANCE_OF_HASH
                                            + Numeric.cleanHexPrefix(address)),
                            DefaultBlockParameterName.LATEST).sendAsync().get())
                    .map(EthCall::getResult).filter(StringUtils::isNotBlank)
                    .map(Numeric::decodeQuantity).map(e -> BnbUtil.fromWei(e, decimals))
                    .orElse(BigDecimal.ZERO);
        } catch (Exception e) {
            logger.info("address: {} handler faided", address, e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {

    }

    @Override
    public BigDecimal getAddressFreeBalance(String address) {
        return null;
    }
}
