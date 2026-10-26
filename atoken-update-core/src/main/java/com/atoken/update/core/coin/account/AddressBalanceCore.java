package com.atoken.update.core.coin.account;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.entity.coin.account.ImportAddressBean;
import com.atoken.update.enums.ImportAddressStatus;
import com.atoken.update.handler.coin.account.BalanceAfterHander;
import com.atoken.update.service.coin.account.AccountAddressService;
import com.atoken.update.service.coin.account.BlockHandlerService;
import com.atoken.update.service.coin.account.AccountExchangeService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public abstract class AddressBalanceCore implements AtokenLogger {
    protected BlockHandlerService blockHandlerService;
    protected BlockchainConfig blockchainConfig;
    @Autowired
    private AccountAddressService addressService;
    @Autowired
    private AccountExchangeService exchangeService;

    private List<String> pledgeCoins = Arrays.asList("KAR","KSM");

    public AddressBalanceCore(BlockHandlerService blockHandlerService, BlockchainConfig blockchainConfig) {
        this.blockHandlerService = blockHandlerService;
        this.blockchainConfig = blockchainConfig;
    }

    public void addressBalanceHandler() {
        // 获取需要处理的导入地址列表
        List<ImportAddressBean> importAddressBeans = exchangeService.selectImportAddressByCoinType(blockchainConfig.getMainAsset().toUpperCase());
        importAddressBeans.forEach(importAddressBean -> {
            try {
                // 通过地址获取余额
                BigDecimal balance = null;
                if(pledgeCoins.contains(blockchainConfig.getMainAsset().toUpperCase())){
                    balance = blockHandlerService.getAddressFreeBalance(importAddressBean.getAddr());
                } else {
                    balance = blockHandlerService.getAddressBalance(importAddressBean.getAddr());
                }
                addressService.addressAmountHander(blockchainConfig.getTablePrefix(), importAddressBean, balance, balanceAfterHander());
            } catch (Exception e) {
                logger.error("更新地址： {}余额失败, {}", importAddressBean.getAddr(), e.getLocalizedMessage());
                Integer count = importAddressBean.getCount() + 1;
                if (count < 3) {
                    exchangeService.updateImportAddressStatus(ImportAddressStatus.UNTREATED.status(), count, importAddressBean.getId());
                } else {
                    exchangeService.updateImportAddressStatus(ImportAddressStatus.TREATED_FAIL.status(), count, importAddressBean.getId());
                }
            }
        });
    }


    public abstract BalanceAfterHander balanceAfterHander();
}
