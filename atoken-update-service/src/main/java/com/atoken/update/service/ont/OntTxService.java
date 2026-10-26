package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.Address;
import com.atoken.update.entity.ont.Block;
import com.atoken.update.entity.ont.Tx;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OntTxService {
    Integer insertTx(Tx tx);

    Integer updateTx(Tx tx);

    Integer updateTxConfirm(String blockHash);

    Integer updateTxUnblock(String blockHash);

    Integer deleteTxByBlockHash(String blockHash);

    Integer saveTx(List<Address> addressList, Tx tx);

    Integer saveTx(List<Address> addressList, Tx tx, Map<String, Map<String, BigDecimal>> addressBalanceMap);

    void rollBackTx(Block parentBlock);

    List<Tx> selectTxBtBlockHash(String blockHash);

    Integer existTx(String txHash, Integer txNum);
}
