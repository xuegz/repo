package com.atoken.update.atom.core;

import com.atoken.blockchain.http.request.account.atom.AtomClient;
import com.atoken.blockchain.http.request.enums.AtomTxType;
import com.atoken.blockchain.utils.VerifyUtil;
import com.atoken.update.atom.common.AddressAmountHander;
import com.atoken.update.atom.common.AtomTransferMessage;
import com.atoken.update.core.BlockTxCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.TransferMessage;
import com.atoken.update.entity.atom.AtomAmountInfo;
import com.atoken.update.entity.atom.AtomDelegateDetail;
import com.atoken.update.handler.RollBackTxAfterHander;
import com.atoken.update.handler.TxAfterHandler;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.TxService;
import com.atoken.update.service.atom.AtomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

//@Service("atomBlockTxCore")
public class AtomBlockTxCore extends BlockTxCore {
    @Autowired
    private AtomService atomService;
    @Autowired
    private AtomClient atomClient;
    private TransferMessage transferMessage;

    @Autowired
    public AtomBlockTxCore(@Qualifier("atomBlockHandlerService") BlockHandlerService blockHandlerService,
                           @Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig,
                           @Qualifier("txService") TxService txService) {
        super(blockHandlerService, blockchainConfig, txService);
    }

    @Override
    public TransferMessage getTransferMessage() {
        if (Objects.isNull(transferMessage)) {
            synchronized (this) {
                if (Objects.isNull(transferMessage)) {
                    transferMessage = new AtomTransferMessage();
                }
            }
        }
        return transferMessage;
    }

    @Override
    public TxAfterHandler txAfterHandler() {
        return (fromAddress, toAddress, tx) -> {
            // 如果from在我们的库里，则更新库里的金额
            if (Objects.nonNull(fromAddress)) {
                AddressAmountHander.addressAmountHandler(fromAddress.getAddr(), atomClient, atomService);
            }

            // 如果to在我们的库里，则更新库里的金额
            if (Objects.nonNull(toAddress)) {
                AddressAmountHander.addressAmountHandler(toAddress.getAddr(), atomClient, atomService);
            }

            // 如果是质押的交易，则更新质押细节表
            if (AtomTxType.MSGDELEGATE.type().equals(tx.getType())
                    || AtomTxType.MSGUNDELEGATE.type().equals(tx.getType())) {
                AtomAmountInfo atomAmountInfo = AddressAmountHander.getDelegateDetailAmount(tx.getFromAddr(), tx.getToAddr(), atomClient);
                AtomDelegateDetail atomDelegateDetail = new AtomDelegateDetail();
                atomDelegateDetail.setWalletId(fromAddress.getWalletId());
                atomDelegateDetail.setAddr(fromAddress.getAddr());
                atomDelegateDetail.setValidatorAddr(tx.getToAddr());
                atomDelegateDetail.setDelegateAmount(atomAmountInfo.getDelegationAmount());
                atomDelegateDetail.setUnbondingAmount(atomAmountInfo.getUnBondingAmount());
                if (AtomTxType.MSGDELEGATE.type().equals(tx.getType())) {
                    Integer row = atomService.insertOrUpdateDelegateDetail(atomDelegateDetail);
                    VerifyUtil.numberNotZore(row, "插入质押细节表失败");
                } else {
                    Integer row = atomService.updateDelegateDetail(atomDelegateDetail);
                    VerifyUtil.numberNotZore(row, "更新质押细节表失败");
                }
            }
        };
    }

    @Override
    public RollBackTxAfterHander rollBackTxAfterHander() {
        return null;
    }
}
