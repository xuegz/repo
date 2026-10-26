package com.atoken.update.service.impl;

import com.atoken.update.dao.ExchangeDao;
import com.atoken.update.entity.CoinFee;
import com.atoken.update.entity.DataDict;
import com.atoken.update.entity.ImportAddressBean;
import com.atoken.update.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    @Autowired
    private ExchangeDao exchangeDao;
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
    public List<ImportAddressBean> selectAllImportAddressByCoinType(String coinType) {
        return this.exchangeDao.selectAllImportAddressByCoinType(coinType);
    }

    @Override
    public Integer updateImportAddressStatus(Integer status, Integer count, Integer id) {
        return exchangeDao.updateImportAddressStatus(status, count, id);
    }

    @Override
    public DataDict selectDataDictByKey(String coinType) {
        return exchangeDao.selectDataDictByKey(coinType);
    }
}
