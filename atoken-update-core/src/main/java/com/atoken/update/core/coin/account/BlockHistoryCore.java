package com.atoken.update.core.coin.account;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.service.coin.account.AccountBlockService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class BlockHistoryCore implements AtokenLogger {
    @Autowired
    private AccountBlockService accountBlockService;

    public void blockHistoryHandler(BlockchainConfig blockchainConfig) {
        logger.info("区块信息历史数据迁移开始");
        // 计算需要归档的日期
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime targetTime = currentTime.minusDays(blockchainConfig.getBlockHistoryRetainTime());
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = targetTime.atZone(zoneId);
        Date targetDate = Date.from(zdt.toInstant());
        // 将区块数据归档
        accountBlockService.blockHistoryMigrate(blockchainConfig.getTablePrefix(), targetDate);
        logger.info("区块信息历史数据迁移结束");
    }

}
