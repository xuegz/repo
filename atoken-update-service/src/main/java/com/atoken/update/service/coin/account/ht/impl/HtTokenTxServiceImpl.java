package com.atoken.update.service.coin.account.ht.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.atoken.update.dao.coin.account.ht.HtTokenTxDao;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.ht.HtTokenTx;
import com.atoken.update.service.coin.account.ht.HtTokenTxService;

@Service
public class HtTokenTxServiceImpl implements HtTokenTxService {

    @Autowired
    private HtTokenTxDao htTokenTxDao;

    @Override
    public int saveOrUpdate(HtTokenTx tokenTx) {
        return htTokenTxDao.saveOrUpdate(tokenTx);
    }

    @Override
    public int removeUnconfirmByTxHash(String txHash) {
        return htTokenTxDao.deleteUnconfirmByTxHash(txHash);
    }

    private boolean isNotEmpty(Collection<?> c) {
        return Objects.nonNull(c) && !c.isEmpty();
    }

    @Override
    @Transactional
    public void rollBackTx(Block parentBlock) {
        Optional.ofNullable(htTokenTxDao.selectListByBlockHash(parentBlock.getHash()))
                .filter(this::isNotEmpty).map(Collection::stream)
                .map(stream -> stream.map(HtTokenTx::getId).collect(Collectors.toList()))
                .ifPresent(this.htTokenTxDao::deleteByIds);
    }
}
