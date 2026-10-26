package com.atoken.update.service.coin.account.ht.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atoken.update.dao.coin.account.ht.HtTokenDao;
import com.atoken.update.entity.coin.account.ht.HtToken;
import com.atoken.update.service.coin.account.ht.HtTokenService;

@Service
public class HtTokenServiceImpl implements HtTokenService {

    @Autowired
    private HtTokenDao htTokenDao;

    @Override
    public HtToken getOneByContractAddr(String contractAddr) {
        return htTokenDao.selectOneByContractAddr(contractAddr);
    }

    @Override
    public int saveOrUpdate(HtToken token) {
        return htTokenDao.saveOrUpdate(token);
    }
}
