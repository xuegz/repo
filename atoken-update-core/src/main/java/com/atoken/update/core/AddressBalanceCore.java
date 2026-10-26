package com.atoken.update.core;

import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.entity.ImportAddressBean;
import com.atoken.update.enums.ImportAddressStatus;
import com.atoken.update.handler.BalanceAfterHander;
import com.atoken.update.service.AddressService;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.ExchangeService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public abstract class AddressBalanceCore implements AtokenLogger {
    protected BlockHandlerService blockHandlerService;
    protected BlockchainConfig blockchainConfig;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ExchangeService exchangeService;

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
                BigDecimal balance = blockHandlerService.getAddressBalance(importAddressBean.getAddr());
                addressService.addressAmountHander(blockchainConfig.getDataBase(), importAddressBean, balance, balanceAfterHander());
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
