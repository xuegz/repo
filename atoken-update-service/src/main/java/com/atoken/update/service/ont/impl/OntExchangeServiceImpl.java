package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntExchangeDao;
import com.atoken.update.entity.ont.CoinFee;
import com.atoken.update.service.ont.OntExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OntExchangeServiceImpl implements OntExchangeService {
    @Autowired
    private OntExchangeDao exchangeDao;
    @Override
    public Integer updateCionFee(CoinFee coinFee) {
        return exchangeDao.updateCionFee(coinFee);
    }

    @Override
    public Integer insertCoinFee(CoinFee coinFee) {
        return exchangeDao.insertCoinFee(coinFee);
    }
}
