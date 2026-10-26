package com.atoken.update.service.coin.account.impl;

import com.atoken.update.dao.coin.account.AccountAddressDao;
import com.atoken.update.dao.coin.account.AccountExchangeDao;
import com.atoken.update.entity.coin.account.Address;
import com.atoken.update.entity.coin.account.ImportAddressBean;
import com.atoken.update.enums.CoinTypeEnum;
import com.atoken.update.enums.ImportAddressStatus;
import com.atoken.update.handler.coin.account.BalanceAfterHander;
import com.atoken.update.service.coin.account.AccountAddressService;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountAddressServiceImpl implements AccountAddressService {
    @Autowired
    private AccountAddressDao addressDao;
    @Autowired
    private AccountExchangeDao exchangeDao;

    @Override
    public List<Address> selectAddressByAddress(String coin, String fromAddr, String toAddr) {
        return addressDao.selectAddressByAddress(coin, fromAddr, toAddr);
    }

    @Override
    public List<Address> selectaddressbyaddr(String coin, String addr) {
        return addressDao.selectAddressByAddr(coin, addr);
    }

    @Override
    public Integer updateAmountByAddress(String coin, String address, BigDecimal amount) {
        return addressDao.updateAmountByAddress(coin, address, amount);
    }

    @Override
    @Transactional
    public void addressAmountHander(String coin, ImportAddressBean importAddress, BigDecimal amount, BalanceAfterHander balanceAfterHander) {
        Integer row = addressDao.updateAmountByAddress(coin, importAddress.getAddr(), amount);
        VerifyUtil.numberNotZore(row, String.format("数据库：%s， 地址：%s，余额：%s，更新失败", coin, importAddress.getAddr(), amount));
        if(!CoinTypeEnum.contains(coin)){
            balanceAfterHander.handler(importAddress.getAddr(), importAddress.getWalletId());
        }
        exchangeDao.updateImportAddressStatus(ImportAddressStatus.TREATED.status(), importAddress.getCount(), importAddress.getId());
    }
}
