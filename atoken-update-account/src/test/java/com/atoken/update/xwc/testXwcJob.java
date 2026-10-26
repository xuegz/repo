package com.atoken.update.xwc;
import com.atoken.BaseTest;
import com.atoken.update.xwc.jobhandler.XwcAddressBalanceSyncJob;
import com.atoken.update.xwc.jobhandler.XwcBlockInfoSyncJob;
import com.atoken.update.xwc.jobhandler.XwcBlockTxSyncJob;
import com.atoken.update.xwc.jobhandler.XwcBlockUpdaterSyncJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author legends_bai
 * @date 2020/12/10 下午3:10
 */
public class testXwcJob extends BaseTest {

    @Autowired
    private XwcAddressBalanceSyncJob xwcAddressBalanceSyncJob;
    @Autowired
    private XwcBlockInfoSyncJob xwcBlockInfoSyncJob;
    @Autowired
    private XwcBlockUpdaterSyncJob xwcBlockUpdaterSyncJob;
    @Autowired
    private XwcBlockTxSyncJob xwcBlockTxSyncJob;

    @Test
    public void testAddressJob() throws Exception {
        xwcAddressBalanceSyncJob.execute("");
    }
    @Test
    public void testBlockInfoJob() throws Exception {
        xwcBlockInfoSyncJob.execute("");
    }
    @Test
    public void testBlockTxJob() throws Exception {
        xwcBlockTxSyncJob.execute("");
    }
    @Test
    public void testBlockUpdaterJob() throws Exception {
        xwcBlockUpdaterSyncJob.execute("");
    }
}
