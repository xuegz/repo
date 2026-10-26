package com.atoken.update.tomo.config;

import com.atoken.blockchain.http.request.account.tomo.TomoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomoBeanConfig {
    @Value("${blockchain.tomo.scan-url}")
    private String tomoScanUrl;
    @Value("${blockchain.tomo.rpc-url}")
    private String tomoRpcUrl;
    @Value("${blockchain.tomo.api-url}")
    private String tomoApiUrl;
    @Value("${blockchain.tomo.master-url}")
    private String tomoMasterUrl;

    @Bean
    public TomoClient tomoClient() {
        return new TomoClient(null, tomoRpcUrl, tomoScanUrl, tomoApiUrl, tomoMasterUrl);
    }
}
