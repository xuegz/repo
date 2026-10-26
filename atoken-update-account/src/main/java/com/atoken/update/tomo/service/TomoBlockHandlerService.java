package com.atoken.update.tomo.service;

import com.atoken.blockchain.http.request.account.tomo.TomoClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.exception.NodeException;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.tomo.thread.TomoBlockTxThread;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigDecimal;
import java.util.*;

@Service("tomoBlockHandlerService")
public class TomoBlockHandlerService implements BlockHandlerService, AtokenLogger {
    @Autowired
    private TomoClient tomoClient;
    @Autowired
    private TomoBlockTxThread tomoBlockTxThread;
    @Override
    public Integer getBlockNumber() {
        return tomoClient.getCurrentBlockHeight();
    }

    @Override
    public List<Block> getBlocks(List<Integer> blockHeights) {
        List<Block> blockList = new LinkedList<>();
        blockHeights.stream().forEach(blockHeight -> {
            logger.info("block: {} start handler", blockHeight);
            // 获取区块信息
            EthBlock.Block tomoBlockInfo = tomoClient.getBlockByHeight(blockHeight);
            Block block = new Block();
            block.setHash(tomoBlockInfo.getHash());
            block.setHeight(blockHeight);
            block.setPrevious(tomoBlockInfo.getParentHash());
            block.setSize(tomoBlockInfo.getSize().intValue());
            block.setTimestamp(tomoBlockInfo.getTimestamp().longValue());
            block.setTxCount(tomoBlockInfo.getTransactions().size());
            block.setState((short)1);
            blockList.add(block);
            logger.info("block: {} handler end", blockHeight);
        });
        return blockList;
    }

    @Override
    public List<Tx> getBlockTx(Block block) {
        try{
            tomoBlockTxThread.init(block);
            tomoBlockTxThread.execute();
            tomoBlockTxThread.await();
            return tomoBlockTxThread.getTxList();
        }catch (Exception e) {
            throw new NodeException(e);
        }
    }

    @Override
    public String getBlockHash(Integer blockHeight) {
        return tomoClient.getBlockHeadByHeight(blockHeight).getHash();
    }

    @Override
    public Integer getMemPoolTxCount() {
        return tomoClient.getPeddingTxs().size();
    }

    @Override
    public BigDecimal getAddressBalance(String address) {
        return DataUtil.formatAmount(tomoClient.getBalanceByAddress(address), CoinEnum.tomo.decimals());
    }

    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {

    }

    @Override
    public BigDecimal getAddressFreeBalance(String address) {
        return null;
    }
}
