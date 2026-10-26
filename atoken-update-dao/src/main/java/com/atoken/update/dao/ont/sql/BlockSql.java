package com.atoken.update.dao.ont.sql;

import com.atoken.update.entity.ont.Block;
import com.atoken.update.util.ConstantUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class BlockSql {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public String insertBlocksSql(@Param("blockList") List<Block> blockList) {

        StringBuilder sql = new StringBuilder(new SQL() {
            {
                INSERT_INTO(ConstantUtil.TBALE_BLOCK);
                INTO_COLUMNS("hash", "height", "previous", "tx_count", "size", "timestamp");
            }
        }.toString());
        sql.append("values")
                .append(blockList.stream()
                        .map(block -> "('" + block.getHash() + "', "
                                + block.getHeight() + ",'" + block.getPrevious()
                                + "'," + block.getTxCount() + "," + block.getSize()
                                + "," + block.getTimestamp() + ")")
                        .reduce((temp, item) -> temp + "," + item).get());
        logger.debug(sql.toString());
        return sql.toString();
    }
}
