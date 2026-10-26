package com.atoken.ksm;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.account.ksm.sidecar.KsmSidecarClient;
import com.atoken.update.AccountApplication;
import com.atoken.update.jobhandler.coin.account.BlockUpdaterJob;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.kusama.core.KsmAddressBalanceCore;
import com.atoken.update.kusama.core.KsmBlockInfoCore;
import com.atoken.update.kusama.core.KsmBlockTxCore;
import com.atoken.update.kusama.core.KsmValidatorInfoCore;
import com.atoken.update.kusama.jobhandler.KsmBlockTxSyncJob;
import com.atoken.update.kusama.jobhandler.KsmBlockUpdaterJob;
import com.atoken.update.kusama.jobhandler.KsmValidatorInfoSyncJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
public class KsmBlockInfoCoreTest {

    @Autowired
    private KsmBlockInfoCore ksmBlockInfoCore;

    @Autowired
    private KsmAddressBalanceCore ksmAddressBalanceCore;

    @Autowired
    private KsmBlockTxCore ksmBlockTxCore;

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    @Autowired
    private KsmValidatorInfoCore ksmValidatorInfoCore;

    @Autowired
    private KsmSidecarClient ksmSidecarClient;

    @Test
    public void testBlockInfo(){
        System.out.println("====info");
        ksmBlockInfoCore.blockInfoHandler();
    }

    @Test
    public void testAddressBalcance(){
        System.out.println("====address balcance====:");
//        ksmAddressBalanceCore.addressBalanceHandler();
//        ksmAddressBalanceCore.balanceAfterHander();
    }

    @Test
    public void testBlockTx(){
        System.out.println("====ksm block tx====");
        KsmBlockTxSyncJob ksmBlockTxSyncJob = new KsmBlockTxSyncJob(ksmBlockTxCore,ksmBlockchainConfig);
        ksmBlockTxSyncJob.execute("");
        System.out.println("====ksm block tx end====");
    }

    @Test
    public void testUpdateBlock() throws Exception {
        System.out.println("====ksm block update====");
        KsmBlockUpdaterJob ksmBlockUpdaterJob = new KsmBlockUpdaterJob(ksmBlockInfoCore,ksmBlockchainConfig);
        ksmBlockUpdaterJob.execute("8107804");
        System.out.println("====ksm block update end====");
    }

    @Test
    public void testKsmValidatorInfo() throws Exception {
        System.out.println("====ksm block validator====");
        ksmValidatorInfoCore.validatorHandler();
        System.out.println("====ksm block validator====end");
    }

    @Test
    public void testRpc(){
        System.out.println(JSON.toJSONString(this.ksmSidecarClient.blocks(1540453)));
    }
}
