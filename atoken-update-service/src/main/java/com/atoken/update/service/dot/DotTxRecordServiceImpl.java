package com.atoken.update.service.dot;

import com.atoken.update.dao.dot.DotTxRecordDao;
import com.atoken.update.entity.TxRecord;
import com.atoken.update.service.TxRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author user
 */
@Service("dotTxRecordService")
public class DotTxRecordServiceImpl implements TxRecordService {

    @Autowired
    private DotTxRecordDao dotTxRecordDao;

    @Override
    public Integer batchTxRecord(String coin, List<TxRecord> txRecordList) {
        return this.dotTxRecordDao.batchTxRecord(coin,txRecordList);
    }
}
