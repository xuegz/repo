package com.atoken.update.service;

import com.atoken.update.entity.Block;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.TxRecord;

import java.math.BigDecimal;
import java.util.List;

public interface BlockHandlerService {
    /**
     * 获取区块高度
     *
     * @return 最新区块高度
     */
    Integer getBlockNumber();

    /**
     * 根据高度列表获取区块信息列表
     * @param blockHeights
     * @return
     */
    List<Block> getBlocks(List<Integer> blockHeights);

    /**
     * 根据区块信息获取交易信息
     * @param block
     * @return
     */
    <T extends Tx> List<T> getBlockTx(Block block);

    /**
     * 根据区块高度获取hash
     * @param blockHeight
     * @return
     */
    String getBlockHash(Integer blockHeight);

    /**
     * 获取交易池交易数量
     * @return
     */
    Integer getMemPoolTxCount();

    /**
     * 获取地址余额
     * @param address
     * @return
     */
    BigDecimal getAddressBalance(String address);

    /**
     * 批量处理交易记录
     * @param txRecordList
     */
    void batchHandlerTxRecord(List<TxRecord> txRecordList);
}
