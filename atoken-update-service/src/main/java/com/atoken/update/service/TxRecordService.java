package com.atoken.update.service;

import com.atoken.update.entity.TxRecord;

import java.util.List;

public interface TxRecordService {

    /**
     * 批量处理交易记录
     * @param coin
     * @param txRecordList
     * @return
     */
    Integer batchTxRecord(String coin,List<TxRecord> txRecordList);
}
