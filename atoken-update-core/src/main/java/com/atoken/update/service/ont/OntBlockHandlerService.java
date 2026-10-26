package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.Tx;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author dfg
 */
public interface OntBlockHandlerService {
    /**
     * 获取区块高度
     * @return
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
    List<Tx> getBlockTx(Block block);

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
     * 获取地址余额map
     * @param address
     * @param ontTokenService
     * @return
     */
    Map<String, BigDecimal> getAddressBalance(String address, OntTokenService ontTokenService);
}
