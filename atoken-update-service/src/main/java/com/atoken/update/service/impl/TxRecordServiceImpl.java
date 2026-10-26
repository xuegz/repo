package com.atoken.update.service.impl;

import com.atoken.update.dao.TxRecordDao;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.service.TxRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("txRecordService")
public class TxRecordServiceImpl implements TxRecordService {

    @Autowired
    private TxRecordDao txRecordDao;

    @Override
    public Integer batchTxRecord(String coin,List<TxRecord> txRecordList) {
        return this.txRecordDao.batchTxRecord(coin,txRecordList);
    }
}
