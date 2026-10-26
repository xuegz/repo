package com.atoken.update.dot.service;

import com.atoken.blockchain.http.request.account.dot.DotBlockInfo;
import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.dot.thread.DotBlockTxThread;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.exception.NodeException;
import com.atoken.update.service.BlockHandlerService;
import org.polkadot.types.type.Extrinsic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 重构之后逻辑 DotSidecarBlockHandlerService
 */
@Deprecated
@Service("dotBlockHandlerService")
public class DotBlockHandlerService implements BlockHandlerService {
    @Autowired
    private DotClient dotClient;
    @Autowired
    private DotBlockTxThread dotBlockTxThread;

    @Override
    public Integer getBlockNumber() {
        String blockHash = dotClient.getLastBlockHash().getResult();
        return dotClient.getBlockHeader(blockHash).getResult().getNumber();
    }

    @Override
    public List<Block> getBlocks(List<Integer> blockHeights) {
        List<Block> blockList = new ArrayList<>();
        blockHeights.forEach(blockHeight -> {
            String blockHash = dotClient.getBlockHash(blockHeight).getResult();
            DotBlockInfo dotBlockInfo = dotClient.getBlock(blockHash).getResult();
            Long timestamp = (Long)new Extrinsic(dotBlockInfo.getBlock().getExtrinsics().get(0)).getMethod().getArgs().get(0).toJson();
            Block block = new Block();
            block.setHash(blockHash);
            block.setHeight(blockHeight);
            block.setPrevious(dotBlockInfo.getBlock().getHeader().getParentHash());
            block.setSize(0);
            block.setTimestamp(timestamp);
            block.setTxCount(dotBlockInfo.getBlock().getExtrinsics().size() - 2);
            block.setState((short)1);
            blockList.add(block);
        });
        return blockList;
    }

    @Override
    public List<Tx> getBlockTx(Block block) {
        try{
            dotBlockTxThread.init(block);
            dotBlockTxThread.execute();
            dotBlockTxThread.await();
            return dotBlockTxThread.getTxList();
        }catch (Exception e) {
            throw new NodeException(e);
        }
    }

    @Override
    public String getBlockHash(Integer blockHeight) {
        return dotClient.getBlockHash(blockHeight).getResult();
    }

    @Override
    public Integer getMemPoolTxCount() {
        return dotClient.getPendingExtrinsics().size();
    }

    @Override
    public BigDecimal getAddressBalance(String address) {
        return DataUtil.formatAmount(dotClient.getBalance(address), CoinEnum.dot.decimals());
    }

    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {

    }
}
