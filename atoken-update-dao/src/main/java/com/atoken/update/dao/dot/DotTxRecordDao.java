package com.atoken.update.dao.dot;

import com.atoken.update.entity.TxRecord;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Mapper
public interface DotTxRecordDao {

    @InsertProvider(type = DotSqlProvider.class,method = "batchTxRecordSql")
    Integer batchTxRecord(@Param("dataBase") String dataBase, @Param("txRecordList") List<TxRecord> txRecordList);

    class DotSqlProvider {
        public String batchTxRecordSql(@Param("dataBase") String dataBase,@Param("txRecordList") List<TxRecord> txRecordList){
            StringBuilder sql = new StringBuilder(new SQL() {
                {
                    INSERT_INTO(dataBase+"." + ConstantUtil.TABLE_TX_RECORD);
                    INTO_COLUMNS("`nonce`, `block_hash`, `from`, `raw`, `standardV`, `gas`, `value`, `block_number`, `to`, `input`, `public_key`, `transaction_index`, `hash`, `gas_price`, `gas_used`, `status`, `time`, `mark_state`, `fee`, `is_token`, `create_time`, `update_time`");
                }
            }.toString());
            sql.append(" VALUES ");
            sql.append(txRecordList.stream().filter(Objects::nonNull).map(txRecord ->
                    "("+txRecord.getNonce()+", '"+txRecord.getBlockHash()+"','"+txRecord.getFrom()+"','"+ txRecord.getRaw() +"',"
                            +txRecord.getStandardV()+","+txRecord.getGas()+",'"+txRecord.getValue()+"',"+txRecord.getBlockNumber()
                            +",'"+txRecord.getTo()+"',"+txRecord.getInput()+","+txRecord.getPublicKey()+","
                            +txRecord.getTransactionIndex()+",'"+txRecord.getHash()+"',"+txRecord.getGasPrice()+","
                            +txRecord.getGasUsed()+","+txRecord.getStatus()+","+txRecord.getTime()+","+txRecord.getMarkState()+","
                            +txRecord.getFee()+","+txRecord.getIsToken()+",'"+txRecord.getCreateTime()+"','"+txRecord.getUpdateTime()
                            +"')"
            ).reduce((temp,item)->temp+","+item).get());
            sql.append("ON DUPLICATE KEY UPDATE update_time = '"+ LocalDateTime.now()+"'");
            return sql.toString();
        }
    }
}
