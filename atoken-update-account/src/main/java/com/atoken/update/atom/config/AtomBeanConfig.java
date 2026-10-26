package com.atoken.update.atom.config;

import com.atoken.blockchain.http.request.account.atom.AtomClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtomBeanConfig {
    @Value("${blockchain.atom.host}")
    private String atomHost;
    @Value("${blockchain.atom.rest-port}")
    private String atomRestPort;
    @Value("${blockchain.atom.rpc-port}")
    private String atomRpcPort;
    @Value("${blockchain.atom.browser-url}")
    private String atomBrowserUrl;

    @Bean
    public AtomClient atomClient() {
        return new AtomClient(atomHost, atomRpcPort, atomRestPort, atomBrowserUrl);
    }
}
