package com.atoken.update.bnb;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.atoken.BaseTest;
import com.atoken.update.bnb.jobhandler.BnbAddressBalanceSyncJob;
import com.atoken.update.bnb.jobhandler.BnbBlockInfoSyncJob;
import com.atoken.update.bnb.jobhandler.BnbBlockTxSyncJob;
import com.atoken.update.bnb.jobhandler.BnbBlockUpdaterSyncJob;


/**
 * @author atoken-team
 * @date 2021/02/26
 */
public class TexsBnbJob extends BaseTest {

    @Autowired
    private BnbAddressBalanceSyncJob bnbAddressBalanceSyncJob;
    @Autowired
    private BnbBlockInfoSyncJob bnbBlockInfoSyncJob;
    @Autowired
    private BnbBlockUpdaterSyncJob bnbBlockUpdaterSyncJob;
    @Autowired
    private BnbBlockTxSyncJob bnbBlockTxSyncJob;

    @Test
    public void testAddressJob() throws Exception {
        bnbAddressBalanceSyncJob.execute("");
    }

    @Test
    public void testBlockInfoJob() throws Exception {
        bnbBlockInfoSyncJob.execute("");
    }

    @Test
    public void testBlockTxJob() throws Exception {
        bnbBlockTxSyncJob.execute("");
    }

    @Test
    public void testBlockUpdaterJob() throws Exception {
        bnbBlockUpdaterSyncJob.execute("");
    }
}
