package com.atoken.update.service;

import com.atoken.update.entity.Address;
import com.atoken.update.entity.Block;
import com.atoken.update.entity.TransferMessage;
import com.atoken.update.entity.Tx;
import com.atoken.update.handler.RollBackTxAfterHander;
import com.atoken.update.handler.TxAfterHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TxService {
    Integer insertTx(String dataBase, Tx tx);

    Integer updateTx(String dataBase, Tx tx);

    Integer updateTxConfirm(String dataBase, String blockHash);

    Integer updateTxUnblock(String dataBase, String blockHash);

    Integer deleteTxByBlockHash(String dataBase, String blockHash);

    Integer saveTx(String dataBase, List<Address> addressList, Tx tx, Map<String, BigDecimal> addressBalanceMap, TxAfterHandler txAfterHandler, TransferMessage transferMessage);

    void rollBackTx(String dataBase, Block parentBlock, RollBackTxAfterHander rollBackTxAfterHander);

    List<Tx> selectTxBtBlockHash(String dataBase, String blockHash);

    Integer existTx(String dataBase, String txHash, Integer txNum);
}
