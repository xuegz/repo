package com.atoken.update.atom.core;

import com.atoken.blockchain.http.request.account.atom.AtomClient;
import com.atoken.blockchain.http.request.account.atom.AtomDelegationResult;
import com.atoken.update.atom.common.AddressAmountHander;
import com.atoken.update.core.AddressBalanceCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.atom.AtomAmountInfo;
import com.atoken.update.entity.atom.AtomDelegateDetail;
import com.atoken.update.handler.BalanceAfterHander;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.atom.AtomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

//@Service("atomAddressBalanceCore")
public class AtomAddressBalanceCore extends AddressBalanceCore {

    @Autowired
    private AtomService atomService;
    @Autowired
    private AtomClient atomClient;
    @Autowired
    public AtomAddressBalanceCore(@Qualifier("atomBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }

    @Override
    public BalanceAfterHander balanceAfterHander() {
        return (address, walletId) -> {

            AddressAmountHander.addressAmountHandler(address, atomClient, atomService);
            // 获取质押信息
            List<AtomDelegationResult> atomDelegations = atomClient.getDelegations(address);
//            String walletId = atomService.selectWalletIdByAddress(address);

            // 将用户的质押信息入库
            if(!CollectionUtils.isEmpty(atomDelegations)) {
                atomDelegations.forEach(atomDelegation -> {
                    AtomAmountInfo atomAmountInfo = AddressAmountHander.getDelegateDetailAmount(atomDelegation.getDelegation().getDelegatorAddress(), atomDelegation.getDelegation().getValidatorAddress(), atomClient);
                    AtomDelegateDetail atomDelegateDetail = new AtomDelegateDetail();
                    atomDelegateDetail.setWalletId(walletId);
                    atomDelegateDetail.setAddr(atomDelegation.getDelegation().getDelegatorAddress());
                    atomDelegateDetail.setValidatorAddr(atomDelegation.getDelegation().getValidatorAddress());
                    atomDelegateDetail.setDelegateAmount(atomAmountInfo.getDelegationAmount());
                    atomDelegateDetail.setUnbondingAmount(atomAmountInfo.getUnBondingAmount());
                    atomService.insertOrUpdateDelegateDetail(atomDelegateDetail);
                });
            }
        };
    }
}
