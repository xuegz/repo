package com.atoken.update.service.coin.account;

import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.TransferMessage;
import com.atoken.update.entity.coin.account.Tx;
import com.atoken.update.handler.coin.account.RollBackTxAfterHander;
import com.atoken.update.handler.coin.account.TxAfterHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountTxService {
    Integer insertTx(String coin, Tx tx);

    Integer updateTx(String coin, Tx tx);

    Integer updateTxConfirm(String coin, String blockHash);

    Integer updateTxUnblock(String coin, String blockHash);

    Integer deleteTxByBlockHash(String coin, String blockHash);

    Integer saveTx(String coin, List<Address> addressList, Tx tx, Map<String, BigDecimal> addressBalanceMap, TxAfterHandler txAfterHandler, TransferMessage transferMessage);

    void rollBackTx(String coin, Block parentBlock, RollBackTxAfterHander rollBackTxAfterHander);

    List<Tx> selectTxBtBlockHash(String coin, String blockHash);

    Integer existTx(String coin, String txHash, Integer txNum);
}
