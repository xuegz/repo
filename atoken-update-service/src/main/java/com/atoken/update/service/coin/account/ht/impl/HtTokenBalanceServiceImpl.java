package com.atoken.update.service.coin.account.ht.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.atoken.update.dao.coin.account.ht.HtTokenBalanceDao;
import com.atoken.update.entity.coin.account.ht.HtTokenBalance;
import com.atoken.update.service.coin.account.ht.HtTokenBalanceService;

@Service
public class HtTokenBalanceServiceImpl implements HtTokenBalanceService {

    @Autowired
    private HtTokenBalanceDao htTokenBalanceDao;

    @Override
    public HtTokenBalance getOneByContractAddrAndAddr(String contractAddr, String addr) {
        return htTokenBalanceDao.selectOneByContractAddrAndAddr(contractAddr, addr);
    }

    @Override
    public List<HtTokenBalance> getListByContractAddrAndAddrs(String contractAddr,
            List<String> addrs) {
        if (CollectionUtils.isEmpty(addrs)) {
            return Collections.emptyList();
        }
        return htTokenBalanceDao.selectListByContractAddrAndAddrs(contractAddr, addrs);
    }

    @Override
    public int saveOrUpdate(HtTokenBalance tokenBalance) {
        return htTokenBalanceDao.saveOrUpdate(tokenBalance);
    }

    @Override
    public int updateById(HtTokenBalance tokenBalance) {
        return htTokenBalanceDao.updateById(tokenBalance);
    }
}
