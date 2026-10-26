package com.atoken.update.service.coin.account.bnb.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.atoken.update.dao.coin.account.bnb.BnbTokenBalanceDao;
import com.atoken.update.entity.coin.account.bnb.BnbTokenBalance;
import com.atoken.update.service.coin.account.bnb.BnbTokenBalanceService;

@Service
public class BnbTokenBalanceServiceImpl implements BnbTokenBalanceService {

    @Autowired
    private BnbTokenBalanceDao bnbTokenBalanceDao;

    @Override
    public BnbTokenBalance getOneByContractAddrAndAddr(String contractAddr, String addr) {
        return bnbTokenBalanceDao.selectOneByContractAddrAndAddr(contractAddr, addr);
    }

    @Override
    public List<BnbTokenBalance> getListByContractAddrAndAddrs(String contractAddr,
            List<String> addrs) {
        if (CollectionUtils.isEmpty(addrs)) {
            return Collections.emptyList();
        }
        return bnbTokenBalanceDao.selectListByContractAddrAndAddrs(contractAddr, addrs);
    }

    @Override
    public int saveOrUpdate(BnbTokenBalance tokenBalance) {
        return bnbTokenBalanceDao.saveOrUpdate(tokenBalance);
    }

    @Override
    public int updateById(BnbTokenBalance tokenBalance) {
        return bnbTokenBalanceDao.updateById(tokenBalance);
    }
}
