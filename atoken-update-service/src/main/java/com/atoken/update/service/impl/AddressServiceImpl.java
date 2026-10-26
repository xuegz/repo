package com.atoken.update.service.impl;

import com.atoken.update.dao.AddressDao;
import com.atoken.update.dao.ExchangeDao;
import com.atoken.update.entity.Address;
import com.atoken.update.entity.ImportAddressBean;
import com.atoken.update.enums.ImportAddressStatus;
import com.atoken.update.handler.BalanceAfterHander;
import com.atoken.update.service.AddressService;
import com.atoken.update.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private ExchangeDao exchangeDao;

    @Override
    public List<Address> selectAddressByAddress(String dataBase, String fromAddr, String toAddr) {
        return addressDao.selectAddressByAddress(dataBase, fromAddr, toAddr);
    }

    @Override
    public Integer updateAmountByAddress(String dataBase, String address, BigDecimal amount) {
        return addressDao.updateAmountByAddress(dataBase, address, amount);
    }

    @Override
    @Transactional
    public void addressAmountHander(String dataBase, ImportAddressBean importAddress, BigDecimal amount, BalanceAfterHander balanceAfterHander) {
        // 更新地址信息
        Integer row = addressDao.updateAmountByAddress(dataBase, importAddress.getAddr(), amount);
        VerifyUtil.numberNotZore(row, String.format("数据库：%s， 地址：%s，余额：%s，更新失败", dataBase, importAddress.getAddr(), amount));
        // 金额后置处理
        balanceAfterHander.handler(importAddress.getAddr(), importAddress.getWalletId());
        // 更新导入表状态
        exchangeDao.updateImportAddressStatus(ImportAddressStatus.TREATED.status(), importAddress.getCount(), importAddress.getId());
    }
}
