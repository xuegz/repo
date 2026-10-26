package com.atoken.update.service.ont.impl;

import com.atoken.update.dao.ont.OntTxDetailDao;
import com.atoken.update.entity.ont.TxDetail;
import com.atoken.update.service.ont.OntTxDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OntTxDetailServiceImpl implements OntTxDetailService {
    @Autowired
    private OntTxDetailDao txDetailDao;
    @Override
    public Integer insertTxDetail(TxDetail txDetail) {
        return txDetailDao.insertTxDetail(txDetail);
    }

    @Override
    public Integer deleteTxDetailByBlockHash(String blockHash) {
        return txDetailDao.deleteTxDetailByBlockHash(blockHash);
    }
}
