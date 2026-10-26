package com.atoken.update.xwc.service;

import com.atoken.blockchain.http.request.account.xwc.XwcClient;
import com.atoken.blockchain.http.request.account.xwc.response.XwcAddressBalancesResponse;
import com.atoken.blockchain.http.request.account.xwc.response.XwcBlockResponse;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.exception.NodeException;
import com.atoken.update.service.TxRecordService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.xwc.thread.XwcBlockTxThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static com.atoken.update.util.ConstantUtil.XWC_TRANSFER_TIME;

/**
 * @author legends_bai
 * @date 2020/12/9 下午4:51
 */
@Service("xwcBlockHandlerService")
public class XwcBlockHandlerService implements BlockHandlerService, AtokenLogger {

    @Autowired
    private  XwcClient xwcClient;

    @Autowired
    private XwcBlockTxThread xwcBlockTxThread;

    @Autowired
    private TxRecordService txRecordService;

    @Override
    public Integer getBlockNumber() {
        return xwcClient.getInfo().getHeadBlockNum();
    }

    @Override
    public List<Block> getBlocks(List<Integer> blockHeights) {
        List<Block> blockList = new LinkedList<>();
        blockHeights.stream().forEach(blockHeight -> {
            logger.info("block: {} start handler", blockHeight);
            // 获取区块信息
            XwcBlockResponse xwcBlockResponse = xwcClient.getBlock(blockHeight);
            Block block = new Block();
            block.setHash(xwcBlockResponse.getBlockId());
            block.setHeight(xwcBlockResponse.getNumber());
            block.setPrevious(xwcBlockResponse.getPrevious());
            block.setSize(0);
            block.setTimestamp((xwcBlockResponse.getTimestamp().getTime()/1000) + XWC_TRANSFER_TIME);
            block.setTxCount(xwcBlockResponse.getTransactions().size());
            block.setState((short)1);
            blockList.add(block);
            logger.info("block: {} handler end", blockHeight);
        });
        return blockList;
    }
    @Override
    public List<Tx> getBlockTx(Block block) {
        try{
            xwcBlockTxThread.init(block);
            xwcBlockTxThread.execute();
            xwcBlockTxThread.await();
            return xwcBlockTxThread.getTxList();
        }catch (Exception e) {
            throw new NodeException(e);
        }
    }

    @Override
    public String getBlockHash(Integer blockHeight) {
        return xwcClient.getBlock(blockHeight).getBlockId();
    }

    @Override
    public Integer getMemPoolTxCount() {
        //TODO 本次接币不实现
        return null;
    }

    @Override
    public BigDecimal getAddressBalance(String address) {
        List<XwcAddressBalancesResponse> addrBalances = xwcClient.getAddrBalances(address);
        if(addrBalances.stream().findFirst().isPresent()){
            return DataUtil.formatAmountByDecimal(addrBalances.stream()
                    .findFirst().get().getAmount(),8);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public void batchHandlerTxRecord(List<TxRecord> txRecordList) {
        this.txRecordService.batchTxRecord(CoinEnum.xwc.name(),txRecordList);
    }

    @Override
    public BigDecimal getAddressFreeBalance(String address) {
        return null;
    }
}
