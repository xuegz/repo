package com.atoken.update.service.coin.account.impl;

import com.atoken.update.dao.coin.account.AccountExchangeDao;
import com.atoken.update.entity.coin.account.CoinFee;
import com.atoken.update.entity.coin.account.ImportAddressBean;
import com.atoken.update.service.coin.account.AccountExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountExchangeServiceImpl implements AccountExchangeService {
    @Autowired
    private AccountExchangeDao exchangeDao;
    @Override
    public Integer updateCionFee(CoinFee coinFee) {
        return exchangeDao.updateCionFee(coinFee);
    }

    @Override
    public Integer insertCoinFee(CoinFee coinFee) {
        return exchangeDao.insertCoinFee(coinFee);
    }

    @Override
    public List<ImportAddressBean> selectImportAddressByCoinType(String coinType) {
        return exchangeDao.selectImportAddressByCoinType(coinType);
    }

    @Override
    public Integer updateImportAddressStatus(Integer status, Integer count, Integer id) {
        return exchangeDao.updateImportAddressStatus(status, count, id);
    }
}
