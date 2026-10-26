package com.atoken.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.http.request.account.ont.OntBlock;
import com.atoken.blockchain.http.request.account.ont.OntClient;
import com.atoken.blockchain.http.request.account.ont.OntSmartCodeEvent;
import com.atoken.blockchain.http.request.account.ont.OntTransfer;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.asset.Transfers;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import io.github.novacrypto.base58.Base58;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OntRequestTest {
    private OntClient ontClient = new OntClient("http://localhost:20336");
    OntSdk ontSdk = OntSdk.getInstance();
    {
        ontSdk.setRpc("http://localhost:8080");
        try {
            ontSdk.setDefaultConnect(ontSdk.getRpc());
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOntClient() throws Exception {

        Block block = ontSdk.getRpc().getBlock(1147653);
        InvokeCode invokeCode = (InvokeCode)block.transactions[0];
        System.out.println("JSON.toJSONString(invokeCode) = " + JSON.toJSONString(invokeCode));
//        Contract contract = Contract.deserializeFrom(invokeCode.code);
//        System.out.println("JSON.toJSONString(contract) = " + JSON.toJSONString(contract));
//        Transfers transfers = Transfers.deserializeFrom(invokeCode.getHashData());
//        System.out.println("JSON.toJSONString(transfers) = " + JSON.toJSONString(transfers));

//        Object obj = ontSdk.getRpc().getSmartCodeEvent(1147653);
//        System.out.println("JSON.toJSONString(obj) = " + JSON.toJSONString(obj));
//        Object tran = ontSdk.getRpc().getTransactionJson("07f98ce4a30beb31e4c216d7e99e6aeae85868aa6de07f58467bd86a69220594");
//        JSONObject tokenBalance = (JSONObject) ontSdk.getRpc().getBalance("ASCRKgrQHeoqVLoEaSD1GmmRQEcP2RRJdg");
//        System.out.println("tran = " + tran);
//        Object blockJson = ontSdk.getRpc().getBlockJson(1147653);
//        System.out.println("balance = " + tokenBalance);
//        System.out.println(JSON.toJSONString(block));
//        System.out.println(JSON.toJSONString(blockJson));
//        Object object = ontSdk.getRpc().getContractJson("79a562f84b7954c62331071d818c1dfa4a747692");
//        System.out.println("JSON.toJSONString(object) = " + JSON.toJSONString(object));

    }

    @Test
    public void testOntTransfer() {
        OntTransfer transfer = OntTransfer.parseTransfer(Helper.hexToBytes("00c66b1442a06fd9bd777deeda3b87d19f77c463ec18c46a6a7cc814cbbb1bcf012994f7f97aa504dac617196416c35e6a7cc8048085b50d6a7cc86c51c1087472616e736665721400000000000000000000000000000000000000020068164f6e746f6c6f67792e4e61746976652e496e766f6b65"));
        System.out.println("JSON.toJSONString(transfer) = " + JSON.toJSONString(transfer));
    }

    @Test
    public void testGetBlockCount() {
        Integer height = ontClient.getBlockHeight();
        System.out.println("height = " + height);
    }
    
    @Test
    public void testGetBlockByBlockHeight() {
        OntBlock block = ontClient.getBlockByHeight(1147653);
        System.out.println("JSON.toJSONString(block) = " + JSON.toJSONString(block));
    }
    
    @Test
    public void testGetBlockHashByHeight() {
        String blockHash = ontClient.getBlockHashByHeight(1147653);
        System.out.println("blockHash = " + blockHash);
    }

    @Test
    public void testGetBalanceByAddress() {
        Map<String, BigDecimal> balanceMap = ontClient.getBalanceByAddress("AMrASWcwFUmzpzqd9jN3KWmiSKtn58Fy34");
        System.out.println("JSON.toJSONString(balanceMap) = " + JSON.toJSONString(balanceMap));
    }
    
    @Test
    public void testGetSmartCodeEvent() {
        List<OntSmartCodeEvent> smartCodeEvents = ontClient.getSmartCodeEvent(1147653);
        System.out.println("JSON.toJSONString(smartCodeEvents) = " + JSON.toJSONString(smartCodeEvents));
    }

    @Test
    public void testGetUnboundOng() throws SDKException, ConnectorException, IOException {
        String result = ontSdk.getRpc().getGrantOng("AVugUE1FgpEY8fktqq1ZDiRmgXkiGjZuvM");
        System.out.println("result = " + result);
    }
    
    @Test
    public void testGetExtractableOng() {
        BigDecimal result = ontClient.getExtractableOng("AVugUE1FgpEY8fktqq1ZDiRmgXkiGjZuvM");
        System.out.println("result = " + result);
    }
    
    @Test
    public void creatAccount() throws Exception {
        Account account = ontSdk.getWalletMgr().createAccount("123456");
        System.out.println("JSON.toJSONString(account) = " + JSON.toJSONString(account));
    }

    @Test
    public void preTx() {
        ontClient.preExecuteTx("00d1c5df25acf40100000000000030750000000000007eefbc073454a7b33291b208d34eb4321af3892b7100c66b147eefbc073454a7b33291b208d34eb4321af3892b6a7cc8149b0bc22b3fcd658bb6c1b2d85416d03e37dd9e4a6a7cc8516a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b650001424101935d115a257ce334b7679e5c44ac5202a80aaf5f509d57b95d12fd261f549c3ae815eb2eb8bef37239b69be657af79c7769bfd97b8f2d1e3daed8518ce9bfb6223210316e95ffe1cb923836d7ed4057da9748b0812316f0a6c29b0671280a1a3b566efac");
    }

    @Test
    public void getMemPool() {
        List<Integer> result = ontClient.getMemPoolTxCount();
        System.out.println("result = " + result);
    }
}
