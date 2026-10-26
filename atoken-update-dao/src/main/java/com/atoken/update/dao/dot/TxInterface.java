package com.atoken.update.dao.dot;

import com.atoken.update.entity.Tx;
import org.apache.ibatis.annotations.Param;

public interface TxInterface {
    Integer insertTx(@Param("dataBase") String dataBase, @Param("tx") Tx tx);

}
