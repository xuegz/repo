package com.atoken.update.service.ont;

import com.atoken.update.entity.ont.TxDetail;

public interface OntTxDetailService {
    Integer insertTxDetail(TxDetail txDetail);

    Integer deleteTxDetailByBlockHash(String blockHash);
}
