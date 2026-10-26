package com.atoken.kar;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.account.kar.sidecar.KarSidecarClient;
import com.atoken.update.AccountApplication;
import com.atoken.update.kar.config.KarBlockchainConfig;
import com.atoken.update.kar.core.KarAddressBalanceCore;
import com.atoken.update.kar.core.KarBlockInfoCore;
import com.atoken.update.kar.core.KarBlockTxCore;
import com.atoken.update.kar.jobhandler.KarBlockTxSyncJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
public class KarBlockInfoCoreTest {

    @Autowired
    private KarBlockInfoCore karBlockInfoCore;

    @Autowired
    private KarAddressBalanceCore karAddressBalanceCore;

    @Autowired
    private KarBlockTxCore karBlockTxCore;
//
    @Autowired
    private KarBlockchainConfig karBlockchainConfig;

    @Autowired
    private KarSidecarClient karSidecarClient;
//
//    @Autowired
//    private KaruraValidatorInfoCore karuraValidatorInfoCore;

    @Test
    public void testBlockInfo(){
        System.out.println("====info");
        karBlockInfoCore.blockInfoHandler();

    }

    @Test
    public void testAddressBalcance(){
        System.out.println("====address balcance====:");
        karAddressBalanceCore.addressBalanceHandler();
//        karAddressBalanceCore.balanceAfterHander();
    }

    @Test
    public void testBlockTx(){
        System.out.println("====kar block tx====");
        KarBlockTxSyncJob karBlockTxSyncJob = new KarBlockTxSyncJob(karBlockTxCore, karBlockchainConfig);
        karBlockTxSyncJob.execute("");
        System.out.println("====kar block tx end====");
    }

//    @Test
//    public void testUpdateBlock() throws Exception {
//        System.out.println("====ksm block update====");
//        KsmBlockUpdaterJob ksmBlockUpdaterJob = new KsmBlockUpdaterJob(ksmBlockInfoCore,ksmBlockchainConfig);
//        ksmBlockUpdaterJob.execute("1532834");
//        System.out.println("====ksm block update end====");
//    }

    @Test
    public void test(){
        System.out.println(JSON.toJSONString(this.karSidecarClient.blocks(231182)));;
    }

}
