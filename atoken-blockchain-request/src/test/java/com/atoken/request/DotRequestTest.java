package com.atoken.request;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.account.dot.DotBlockInfo;
import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.blockchain.http.request.account.dot.DotHeader;
import org.junit.Test;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v0.Events;
import org.polkadot.types.type.*;

import java.math.BigDecimal;
import java.util.List;


public class DotRequestTest {

//    rpc 39.96.194.39:9933
//    ws 39.96.194.39:9944
    private DotClient dotClient = new DotClient("http://39.96.194.39:9933");
    private String id;
    private String amount;
    private String reasons;

    @Test
    public void testLastGetBlockHash() {
        String blockHash = dotClient.getLastBlockHash().getResult();
//        0x969c78132fd011b2a49378ce8de27f42ef4904bff0cd4094c80bfb6dcb6f35f1
        System.out.println("blockHash = " + blockHash);
    }
    
    @Test
    public void testGetBlockHash() {
        String blockHash = dotClient.getBlockHash(1402368).getResult();
        System.out.println("blockHash = " + blockHash);
    }
    
    @Test
    public void testGetBlockHeader() {
        DotHeader dotHeader = dotClient.getBlockHeader("0x969c78132fd011b2a49378ce8de27f42ef4904bff0cd4094c80bfb6dcb6f35f1").getResult();
        System.out.println("JSON.toJSONString(dotHeader) = " + JSON.toJSONString(dotHeader));
    }
    
    @Test
    public void testGetBlock() {
        DotBlockInfo dotBlockInfo = dotClient.getBlock("0x969c78132fd011b2a49378ce8de27f42ef4904bff0cd4094c80bfb6dcb6f35f1").getResult();
        Extrinsic extrinsic = new Extrinsic(dotBlockInfo.getBlock().getExtrinsics().get(3));
        System.out.println("JSON.toJSONString(dotBlockInfo) = " + JSON.toJSONString(dotBlockInfo));
    }
    
    @Test
    public void testGetEvent() {
        Vector<EventRecord> eventRecords = dotClient.getEvent("0x969c78132fd011b2a49378ce8de27f42ef4904bff0cd4094c80bfb6dcb6f35f1");
        eventRecords.forEach(eventRecord -> {
            Event event = eventRecord.getEvent();
            String method = event.getMethod();
            String section = event.getSection();
            List<CreateType.TypeDef> types = event.getTypeDef();
            Event.EventIndex index = event.getIndex();
            Events.EventMetadata metadata =  event.getMeta();
            System.out.println("event = " + event);
            System.out.println("eventRecord = " + eventRecord);
        });
        System.out.println("JSON.toJSONString(eventRecords) = " + JSON.toJSONString(eventRecords));
    }

    @Test
    public void testGetNonce() {
        // TODO
        Integer nonce = dotClient.getNonce("1vwCSYHtdtBMm8AaA25TUPGz8DrFMty7PRKyjGbiY1b2BEk");
        System.out.println("nonce = " + nonce);
    }

    @Test
    public void testGetBalance() {
        // TODO
        BigDecimal balance = dotClient.getBalance("1vwCSYHtdtBMm8AaA25TUPGz8DrFMty7PRKyjGbiY1b2BEk");
        System.out.println("balance = " + balance);
    }

    @Test
    public void testGetPendingExtrinsics() {
        List<String> txes = dotClient.getPendingExtrinsics();
        System.out.println("txes = " + txes);
    }

    @Test
    public void testTime() {
        Extrinsic extrinsic = new Extrinsic("0x01000003540d385d");
        Long time = (Long)extrinsic.getMethod().getArgs().get(0).toJson();
        System.out.println("time = " + time);
        System.out.println("extrinsic = " + extrinsic);
    }
    
    @Test
    public void testGetValidators() {
        Vector<AccountId> accountIds = dotClient.getValidators();
        System.out.println("accountIds = " + accountIds);
    }

    @Test
    public void testGetValidatorInfoByValidatorAddr() {
        ValidatorPrefs obj = dotClient.getValidatorInfoByValidatorAddr("5DuiZFa184E9iCwbh4WjXYvJ88NHvWJbS8SARY8Ev1YEqrri");
        System.out.println("obj = " + obj);
        System.out.println("JSON.toJSONString(obj) = " + JSON.toJSONString(obj));
    }

    @Test
    public void testGetStakersByValidatorAddr() {
        Exposure exposure = dotClient.getStakersByValidatorAddr("5DuiZFa184E9iCwbh4WjXYvJ88NHvWJbS8SARY8Ev1YEqrri");
        System.out.println("exposure = " + exposure);
    }

    @Test
    public void testGetLockBalance() {
        BigDecimal balanceLock = dotClient.getLockBalance("5CaBs3veE3wBHH9C5HqrSkp8ksFCzAyhdH37GiGzAV15qBZK");
        System.out.println("balanceLock = " + balanceLock);
    }
    
    @Test
    public void testGetValidatorByDelegatorAddr() {
        Vector<AccountId> accountIds = dotClient.getValidatorsByDelegatorAddr("5FLXS8ZrrzctAdp95Ux6VJaBMt5EQRkiwW2yZZDoi1Ae9Wj2");
        System.out.println("accountIds = " + accountIds);
    }
    
    @Test
    public void testGetLedger() {
        Object obj = dotClient.getLedger("5Gnjj7zU1qviZFXEXgVoNVZW4Hx1VEhnTWLuGxNskKkudHLp");
        System.out.println("obj = " + obj);
    }

    @Test
    public void testGetController() {
        AccountId accountId = dotClient.getControllerByStashAddr("5FLXS8ZrrzctAdp95Ux6VJaBMt5EQRkiwW2yZZDoi1Ae9Wj2");
        System.out.println("accountId = " + accountId.toString());
    }

    @Test
    public void testGetSessionLenghth() {
        BlockNumber sessionLenghth = dotClient.getSessionLenghth();
        System.out.println("accountId = " + sessionLenghth.toString());
    }

    @Test
    public void testGetTotalIssuance() {
        BigDecimal totalIssuance = dotClient.getTotalIssuance();
        System.out.println("accountId = " + totalIssuance.toString());
    }

}
