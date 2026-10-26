package com.atoken.update.atom.common;

import com.atoken.blockchain.http.request.enums.AtomTxType;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.TransferMessage;
import com.atoken.update.entity.Tx;

public class AtomTransferMessage extends TransferMessage {
    @Override
    public TransferMessage toTransferMessage(Tx tx, Address address, boolean isFrom) {
        isFrom = AtomTxType.MSGUNDELEGATE.type().equals(tx.getType()) || AtomTxType.MSGWITHDRAWDELEGATIONREWARD.type().equals(tx.getType()) ? false: isFrom;
        return super.toTransferMessage(tx, address, isFrom);
    }
}
