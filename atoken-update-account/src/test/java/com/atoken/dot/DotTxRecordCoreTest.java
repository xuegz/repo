package com.atoken.dot;

import com.atoken.BaseTest;
import com.atoken.update.dot.config.DotBlockchainConfig;
import com.atoken.update.dot.core.DotTxRecordCore;
import com.atoken.update.dot.jobhandler.DotTxRecordSyncJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DotTxRecordCoreTest extends BaseTest {

    @Autowired
    private DotTxRecordCore dotTxRecordCore;

    @Autowired
    private DotBlockchainConfig dotBlockchainConfig;

    @Test
    public void dotTxRecordTest() throws Exception {
        System.out.println("====任务执行开始");
        DotTxRecordSyncJob dotTxRecordSyncJob = new DotTxRecordSyncJob(dotTxRecordCore,dotBlockchainConfig);
        dotTxRecordSyncJob.execute("");
        System.out.println("====任务执行结束");
    }
}
