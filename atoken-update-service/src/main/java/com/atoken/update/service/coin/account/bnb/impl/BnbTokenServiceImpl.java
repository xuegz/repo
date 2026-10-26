package com.atoken.update.service.coin.account.bnb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atoken.update.dao.coin.account.bnb.BnbTokenDao;
import com.atoken.update.entity.coin.account.bnb.BnbToken;
import com.atoken.update.service.coin.account.bnb.BnbTokenService;

@Service
public class BnbTokenServiceImpl implements BnbTokenService {

    @Autowired
    private BnbTokenDao bnbTokenDao;

    @Override
    public BnbToken getOneByContractAddr(String contractAddr) {
        return bnbTokenDao.selectOneByContractAddr(contractAddr);
    }

    @Override
    public int saveOrUpdate(BnbToken token) {
        return bnbTokenDao.saveOrUpdate(token);
    }
}
