package com.atoken.blockchain.http.request.account.tomo;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.parity.JsonRpc2_0Parity;

import java.util.Arrays;

/**
 * @description: atoken-wallet-eth
 * @author: qingyun.yu
 * @date: 2019-12-27 16:57
 **/
public class ParityExtend extends JsonRpc2_0Parity {
    public ParityExtend(Web3jService web3jService) {
        super(web3jService);
    }

    /**
     * 获取nonce值
     * @param address
     * @return
     */
    public Request<?, StringResponse> parityNetxNonce(String address) {
        return new Request<>(
                "parity_nextNonce",
                Arrays.asList(address),
                web3jService,
                StringResponse.class);
    }

    /**
     * 获取节点交易池中交易列表
     * @return
     */
    public Request<?, StringResponse> ethPendingTransactions() {
        Request<Object, StringResponse> request = new Request<>(
                "eth_pendingTransactions",
                Arrays.asList(),
                web3jService,
                StringResponse.class);
        request.setId(1L);
        return request;
    }

    public static ParityExtend build(Web3jService web3jService) {
        return new ParityExtend(web3jService);
    }
}
