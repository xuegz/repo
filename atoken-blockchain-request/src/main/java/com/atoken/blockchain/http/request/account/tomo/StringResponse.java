package com.atoken.blockchain.http.request.account.tomo;

import org.web3j.protocol.core.Response;

/**
 * @description: atoken-wallet-eth
 * @author: qingyun.yu
 * @date: 2019-12-27 17:02
 **/
public class StringResponse extends Response<String> {
    public String success() {
        return getResult();
    }
}
