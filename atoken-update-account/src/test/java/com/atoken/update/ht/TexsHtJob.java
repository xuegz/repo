package com.atoken.update.ht;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.atoken.BaseTest;
import com.atoken.update.ht.jobhandler.HtAddressBalanceSyncJob;
import com.atoken.update.ht.jobhandler.HtBlockInfoSyncJob;
import com.atoken.update.ht.jobhandler.HtBlockTxSyncJob;
import com.atoken.update.ht.jobhandler.HtBlockUpdaterSyncJob;


/**
 * @author atoken-team
 * @date 2021/02/26
 */
public class TexsHtJob extends BaseTest {

    @Autowired
    private HtAddressBalanceSyncJob htAddressBalanceSyncJob;
    @Autowired
    private HtBlockInfoSyncJob htBlockInfoSyncJob;
    @Autowired
    private HtBlockUpdaterSyncJob htBlockUpdaterSyncJob;
    @Autowired
    private HtBlockTxSyncJob htBlockTxSyncJob;

    @Test
    public void testAddressJob() throws Exception {
        htAddressBalanceSyncJob.execute("");
    }

    @Test
    public void testBlockInfoJob() throws Exception {
        htBlockInfoSyncJob.execute("");
    }

    @Test
    public void testBlockTxJob() throws Exception {
        htBlockTxSyncJob.execute("");
    }

    @Test
    public void testBlockUpdaterJob() throws Exception {
        htBlockUpdaterSyncJob.execute("");
    }
}
