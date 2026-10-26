package com.atoken.update.service.coin.account.bnb.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.atoken.update.dao.coin.account.bnb.BnbTokenTxDao;
import com.atoken.update.entity.coin.account.Block;
import com.atoken.update.entity.coin.account.bnb.BnbTokenTx;
import com.atoken.update.service.coin.account.bnb.BnbTokenTxService;

@Service
public class BnbTokenTxServiceImpl implements BnbTokenTxService {

    @Autowired
    private BnbTokenTxDao bnbTokenTxDao;

    @Override
    public int saveOrUpdate(BnbTokenTx tokenTx) {
        return bnbTokenTxDao.saveOrUpdate(tokenTx);
    }

    @Override
    public int removeUnconfirmByTxHash(String txHash) {
        return bnbTokenTxDao.deleteUnconfirmByTxHash(txHash);
    }

    private boolean isNotEmpty(Collection<?> c) {
        return Objects.nonNull(c) && !c.isEmpty();
    }

    @Override
    @Transactional
    public void rollBackTx(Block parentBlock) {
        Optional.ofNullable(bnbTokenTxDao.selectListByBlockHash(parentBlock.getHash()))
                .filter(this::isNotEmpty).map(Collection::stream)
                .map(stream -> stream.map(BnbTokenTx::getId).collect(Collectors.toList()))
                .ifPresent(this.bnbTokenTxDao::deleteByIds);
    }
}
