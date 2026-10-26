package com.atoken.request;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.RpcParams;
import com.atoken.blockchain.http.request.account.tomo.*;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TomoRequestTest {
//    private Web3j web3j = Web3j.build(new HttpService("https://testnet.tomochain.com"));
    private Web3j web3j = Web3j.build(new HttpService("http://172.17.3.190:8545"));
    private ParityExtend parityExtend = ParityExtend.build(new HttpService("https://rpc.tomochain.com"));
    private TomoClient tomoClient = new TomoClient(null, "https://rpc.tomochain.com", "https://scan.tomochain.com", null, null);

    @Test
    public void testBase() throws IOException {
        EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
        System.out.println("JSON.toJSONString(ethBlockNumber) = " + JSON.toJSONString(ethBlockNumber));
    }

    @Test
    public void testGetCurrentBlockHeight() {
        EthBlockNumber ethBlockNumber = null;
        try {
            ethBlockNumber = web3j.ethBlockNumber().send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON.toJSONString(ethBlockNumber) = " + JSON.toJSONString(ethBlockNumber));
    }

    @Test
    public void testBlockByBlockHeight() {
        EthBlock ethBlockRequest = null;
        try {
            ethBlockRequest = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(
                    3655)), true).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON.toJSONString(ethBlockRequest) = " + JSON.toJSONString(ethBlockRequest));
    }

    @Test
    public void testAddressBalance() {
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = web3j.ethGetBalance("0x71b7b5027afc6b070330123a807187b52276d4ce", DefaultBlockParameter.valueOf("latest")).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON.toJSONString(ethGetBalance) = " + JSON.toJSONString(ethGetBalance));
    }

    @Test
    public void testGetCandidates() {
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("eth_getCandidates");
        List<Object> params = new ArrayList<>();
        params.add("latest");
        rpcParams.setParams(params);
        String response = new Request().url("https://rpc.tomochain.com").method(Method.POST).setContentType(ContentType.JSON_UTF8).data(rpcParams).send(String.class);
        System.out.println("response = " + response);
    }

    @Test
    public void testGetTransactionReceipt() {
        EthGetTransactionReceipt ethGetTransactionReceipt = null;
        try {
            ethGetTransactionReceipt = web3j.ethGetTransactionReceipt("0xbedabf0e7625730626c16bc23bc1a8dbc659054b9b6021bfba98007a5212c3aa").send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("JSON.toJSONString(ethGetTransactionReceipt) = " + JSON.toJSONString(ethGetTransactionReceipt));

    }

    @Test
    public void testGetCandidatess() {
        String response = new Request().url("https://master.tomochain.com", "api/candidates").method(Method.GET).send(String.class);
        System.out.println("response = " + response);
    }

    @Test
    public void testGetCandidatesVoters() {
        String response = new Request().url("https://master.tomochain.com", "api/candidates", "0x70c2967ae1e7ddd2335c36c4a322c90be0bbfe66", "voters").method(Method.GET).send(String.class);
        System.out.println("response = " + response);
    }
    
    @Test
    public void testGetVoterRewards() {
        TomoVoterRewards response = new Request().url("https://scan.tomochain.com", "api/rewards", "0x689c56aef474df92d44a1b70850f808488f9769c").method(Method.GET).send(TomoVoterRewards.class);
        System.out.println("response = " + JSON.toJSONString(response));
    }

    @Test
    public void testGetCandidatesss() throws ExecutionException, InterruptedException {
        Function function = new Function("getCandidates", Collections.<Type>emptyList(), Arrays.asList(new TypeReference<DynamicArray<Address>>() {}));
        String encode = FunctionEncoder.encode(function);
        Transaction ethCallTransaction = Transaction.createEthCallTransaction(null, "0x0000000000000000000000000000000000000088", encode);
        EthCall ethCall = web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        String value = ethCall.getResult();
        List<Type> types = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        System.out.println("value = " + JSON.toJSONString(types.get(0)));

    }

    @Test
    public void testGetWithdrawBlockNumbers() throws ExecutionException, InterruptedException {
        Function function = new Function("getWithdrawBlockNumbers", Arrays.asList(new Address("0x2b04Fe311db599906B22c940e827C304242413A0")), Arrays.asList(new TypeReference<DynamicArray<Uint256>>() {}));
        String encode = FunctionEncoder.encode(function);
        Transaction ethCallTransaction = Transaction.createEthCallTransaction("0x2b04Fe311db599906B22c940e827C304242413A0", "0x0000000000000000000000000000000000000088", encode);
        EthCall ethCall = web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        String value = ethCall.getResult();
        List<Type> types = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        System.out.println("value = " + JSON.toJSONString(types.get(0)));

    }

    @Test
    public void testGetVoters() throws ExecutionException, InterruptedException {
        Function function = new Function("vote", Arrays.asList(new Address("0x7e1e827c7c22834f31075b4530e9e0e2b7815ad8")), Arrays.asList(new TypeReference<DynamicArray<Address>>() {}));
        String encode = FunctionEncoder.encode(function);
        Transaction ethCallTransaction = Transaction.createEthCallTransaction(null, "0x0000000000000000000000000000000000000088", encode);
        EthCall ethCall = web3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        String value = ethCall.getResult();
        System.out.println("value = " + value);
        Function function1 = new Function("getVoters", Arrays.asList(new Address("0x7e1e827c7c22834f31075b4530e9e0e2b7815ad8")), Arrays.asList(new TypeReference<Address>() {}));
        System.out.println(Hash.sha3("vote(address)"));
        List<Type> type = FunctionReturnDecoder.decode("0x0000000000000000000000007e1e827c7c22834f31075b4530e9e0e2b7815ad8", function1.getOutputParameters());
        List<Type> types = FunctionReturnDecoder.decode(value, function.getOutputParameters());
        System.out.println("value = " + JSON.toJSONString(types));
    }

    @Test
    public void testGetNextNonce() throws IOException {
        String nonce = web3j.ethGetTransactionCount("0x920731e34f446bc94ddc8d7e9201ab6d715f46c7", DefaultBlockParameterName.LATEST).send().getResult();
        System.out.println("nonce = " + nonce);
    }

    @Test
    public void testGetRewards() {
        RpcParams rpcParams = new RpcParams();
        rpcParams.setParams(Arrays.asList("0xed68ea9010d9be4aa9d7157e27cfe952c443a4d8efd5af372cb521eb20f457fc"));
        rpcParams.setMethod("eth_getRewardByHash");
        String response = new Request().method(Method.POST).url("https://rpc.tomochain.com").data(rpcParams).setContentType(ContentType.JSON_UTF8).send(String.class);
        System.out.println("response = " + response);
    }

    @Test
    public void testGetVoterVolidaters() {
        String response = new Request().url("https://master.tomochain.com", "api/voters", "0xfc0f31fe4e4b628b1a8a0272febde903167df5fc", "candidates").method(Method.GET).send(String.class);
        System.out.println("response = " + response);
    }

    @Test
    public void testGetPedding() throws IOException {

        com.atoken.blockchain.http.request.Request request = new com.atoken.blockchain.http.request.Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("eth_pendingTransactions");
        List<TomoTransaction> response =  request.url("https://testnet.tomochain.com")
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(TomoTransactionsRespnonse.class)
                .getResult();
        System.out.println("JSON.toJSONString(response) = " + JSON.toJSONString(response));
    }
    
    @Test
    public void testGetTxByTxHash() throws IOException {
        EthTransaction ethTransaction = web3j.ethGetTransactionByHash("0xbedabf0e7625730626c16bc23bc1a8dbc659054b9b6021bfba98007a5212c3aa").send();
        System.out.println("JSON.toJSONString(ethTransaction) = " + JSON.toJSONString(ethTransaction));
    }

    @Test
    public void testSendTx() throws IOException {
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction("0xf86c0c840ee6b28082520894fca27ef55a0b94c8a78fa89c297d7d2b8613f15b880de0b6b3a76400008081d6a09b0896c8e4e37cc1d01e0a78da5224fd7a0add8d1729bd50529837a5bec5aef3a0680ae82468e975896d6ceab049094c353d1ab459d129ccfb2096596e9e68dfae").send();
        System.out.println("JSON.toJSONString(ethSendTransaction) = " + JSON.toJSONString(ethSendTransaction));

    }

    @Test
    public void testGetCandidateCap() {
//        tomoClient.getCandidateCap("0x70c2967ae1e7ddd2335c36c4a322c90be0bbfe66");
        TomoAccount tomoAccount = tomoClient.getAccountInfo("0x48c4eef517b79ff5259374fed4245359d8fb3ea9");
        System.out.println("JSON.toJSONString(tomoAccount) = " + JSON.toJSONString(tomoAccount));
    }

    @Test
    public void testGetYear() {
        Double d = new com.atoken.blockchain.http.request.Request()
                .url("https://master.tomochain.com/", "api/voters/calculatingReward1Day")
                .method(Method.GET)
                .data("candidate", "0x70c2967ae1e7ddd2335c36c4a32c90be0bbfe66", "amount", "100")
                .send(Double.class) * 360/100;
        System.out.println("d = " + d);
    }
}