package com.atoken.update.atom.service;

import com.atoken.blockchain.http.request.account.atom.AtomAmount;
import com.atoken.blockchain.http.request.account.atom.AtomBlockInfo;
import com.atoken.blockchain.http.request.account.atom.AtomClient;
import com.atoken.blockchain.http.request.enums.AtomCoin;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.atom.thread.AtomBlockTxThread;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.atom.AtomTx;
import com.atoken.update.exception.NodeException;
import com.atoken.update.service.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("atomBlockHandlerService")
public class AtomBlockHandlerService implements BlockHandlerService {
    @Autowired
    private AtomClient atomClient;
    @Autowired
    private AtomBlockTxThread atomBlockTxThread;
    @Override
    public Integer getBlockNumber() {
        return atomClient.getCurrentBlockHeight();
    }

    @Override
    public List<Block> getBlocks(List<Integer> blockHeights) {
        List<Block> blockList = new ArrayList<>();
        blockHeights.forEach(blockHeight -> {
            // 从链上获取区块信息
            AtomBlockInfo atomBlockInfo = atomClient.getBlockByHeight(blockHeight);
            Block block = new Block();
            block.setHash(atomBlockInfo.getBlockId().getHash());
            block.setHeight(blockHeight);
            block.setPrevious(atomBlockInfo.getBlock().getHeader().getLastBlockId().getHash());
            block.setSize(0);
            block.setTimestamp(atomBlockInfo.getBlock().getHeader().getTime().getTime()/1000);
            block.setTxCount(atomBlockInfo.getBlock().getData().getTxs().size());
            block.setState((short)1);
            blockList.add(block);
        });
        return blockList;
    }

    @Override
    public List<AtomTx> getBlockTx(Block block) {
        try{
            atomBlockTxThread.init(block);
            atomBlockTxThread.execute();
            atomBlockTxThread.await();
            return atomBlockTxThread.getTxList();
        }catch (Exception e) {
            throw new NodeException(e);
        }
    }

    @Override
    public String getBlockHash(Integer blockHeight) {
        return null;
    }

    @Override
    public Integer getMemPoolTxCount() {
        return atomClient.getAtomMemPoolInfo().getnTxs();
    }

    @Override
    public BigDecimal getAddressBalance(String address) {
        List<AtomAmount> atomAmounts = atomClient.getBalanceByAddress(address);

        return DataUtil.formatAmount(atomAmounts.stream()
                .filter(atomAmount -> AtomCoin.uatom.isCoin(atomAmount.getDenom()))
                .map(atomAmount -> atomAmount.getAmount())
                .findFirst().get(), CoinEnum.atom.decimals());
    }

    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {

    }
}
