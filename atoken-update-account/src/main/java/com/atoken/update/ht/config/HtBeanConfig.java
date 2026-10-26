package com.atoken.update.ht.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class HtBeanConfig {

    @Value("${blockchain.ht.rpc-url:https://http-mainnet-node.huobichain.com}")
    private String htRpcUrl;

    @Bean("htWeb3j")
    public Web3j web3j() {
        return Web3j.build(new HttpService(htRpcUrl));
    }
}
