package com.atoken.update.service.coin.account;

import com.atoken.update.entity.TxRecord;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.Tx;

import java.math.BigDecimal;
import java.util.List;

public interface BlockHandlerService {
    Integer getBlockNumber();

    List<Block> getBlocks(List<Integer> blockHeights);

    <T extends Tx> List<T> getBlockTx(Block block);

    String getBlockHash(Integer blockHeight);

    Integer getMemPoolTxCount();

    BigDecimal getAddressBalance(String address);

    default BigDecimal getTokenBalance(String contractAddr, String address, Integer decimals) {
        return null;
    }

    void batchHandlerTxRecord(List<TxRecord> txRecordList);

    BigDecimal getAddressFreeBalance(String address);
}
