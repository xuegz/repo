package com.atoken.update.bnb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class BnbBeanConfig {

    @Value("${blockchain.bnb.rpc-url:https://bsc-dataseed1.binance.org/}")
    private String bnbRpcUrl;

    @Bean("bnbWeb3j")
    public Web3j web3j() {
        return Web3j.build(new HttpService(bnbRpcUrl));
    }
}
