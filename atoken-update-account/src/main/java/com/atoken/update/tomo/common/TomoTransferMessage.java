package com.atoken.update.tomo.common;

import com.atoken.blockchain.http.request.account.tomo.ContractFunctionEnum;
import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.TransferMessage;
import com.atoken.update.entity.coin.account.Tx;

public class TomoTransferMessage extends TransferMessage {
    @Override
    public TransferMessage toTransferMessage(Tx tx, Address address, boolean isFrom) {
        isFrom = ContractFunctionEnum.WITHDRAW.name().equals(tx.getType()) ? false: isFrom;
        return super.toTransferMessage(tx, address, isFrom);
    }
}
