package com.atoken.update.ont.service;

import com.atoken.blockchain.http.request.account.ont.OntBlock;
import com.atoken.blockchain.http.request.account.ont.OntClient;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.Token;
import com.atoken.update.entity.ont.Tx;


import com.atoken.update.exception.NodeException;
import com.atoken.update.ont.thread.OntBlockTxThread;
import com.atoken.update.service.ont.OntBlockHandlerService;
import com.atoken.update.service.ont.OntTokenService;
import com.atoken.update.util.AtokenLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author dfg
 */
@Service
public class OntBlockHandlerServiceImpl implements OntBlockHandlerService, AtokenLogger {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OntClient ontClient;
    @Autowired
    @Qualifier("tokenMap")
    private Map<String, Token> tokenMap;
    @Autowired
    private OntTokenService ontTokenService;
    @Autowired
    private OntBlockTxThread ontBlockTxThread;

    @Override
    public Integer getBlockNumber() {
        return ontClient.getBlockHeight();
    }

    @Override
    public List<Block> getBlocks(List<Integer> blockNumbers) {
        List<Block> blockList = new ArrayList<>();
        blockNumbers.forEach(blockNumber -> {
            // 从链上获取区块信息
            OntBlock ontBlock = ontClient.getBlockByHeight(blockNumber);
            Block block = new Block();
            block.setHash(ontBlock.getHeader().getHash());
            block.setHeight(blockNumber);
            block.setPrevious(ontBlock.getHeader().getPrevBlockHash());
            block.setSize(ontBlock.getSize());
            block.setTimestamp(ontBlock.getHeader().getTimestamp());
            block.setTxCount(ontBlock.getTransactions().size());
            block.setState((short)1);
            blockList.add(block);
        });
        return blockList;
    }

    @Override
    public List<Tx> getBlockTx(Block block) {
        try{
            // 初始化线程信息
            ontBlockTxThread.init(block);
            // 执行
            ontBlockTxThread.execute();
            // 等待所有的线程运行结束
            ontBlockTxThread.await();

            return ontBlockTxThread.getTxList();
        }catch (Exception e) {
            throw new NodeException(e);
        }
    }

    @Override
    public String getBlockHash(Integer blockHeight) {
        return ontClient.getBlockHashByHeight(blockHeight);
    }

    @Override
    public Integer getMemPoolTxCount() {
        List<Integer> memPoolTxCount = ontClient.getMemPoolTxCount();
        return memPoolTxCount.get(0) + memPoolTxCount.get(1);
    }

    @Override
    public Map<String, BigDecimal> getAddressBalance(String address, OntTokenService ontTokenService) {
        // 通过地址获取余额
        Map<String, BigDecimal> balanceMap = ontClient.getBalanceByAddress(address);
        Map<String, BigDecimal> result = new HashMap<>();
        List<Token> tokenList = ontTokenService.selectTokes();
        Map<String, Token> tokenMap = new HashMap<>();
        tokenList.forEach(token -> {
            tokenMap.put(token.getName(), token);
        });
        // 目前只需要有ont，ong的余额，进行精度换算
        balanceMap.entrySet().forEach(balanceInfo -> {
            if("ont".equals(balanceInfo.getKey())) {
                result.put(tokenMap.get(balanceInfo.getKey()).getContractAddr()
                        ,balanceInfo.getValue());
            } else {
                Token token = tokenMap.get(balanceInfo.getKey());
                if (!Objects.isNull(token)) {
                    result.put(token.getContractAddr()
                            ,DataUtil.formatAmount(balanceInfo.getValue(), token.getDecimals()));
                }
            }
        });
        return result;
    }

    @PostConstruct
    public void init() {
        logger.info("init ont token info start");
        // 初始化支持的币种ont,ong
        List<Token> tokenList = ontTokenService.selectTokes();
        tokenList.forEach(token -> tokenMap.put(token.getContractAddr(), token));
        logger.info("init ont token info end");
    }
}
