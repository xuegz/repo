package com.atoken.update.kar.config;

import com.atoken.blockchain.http.request.account.kar.KarClient;
import com.atoken.blockchain.http.request.account.kar.sidecar.KarSidecarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author evil
 * tip: 因为与dot一样所以client不用重新写
 */
@Configuration
public class KarBeanConfig {

    @Value("${blockchain.kar.host}")
    private String dotHost;
    @Value("${blockchain.kar.rpc-port}")
    private String dotRpcPort;

    @Value("${rpc.kar.url.sidecar_node:http://172.17.3.66:8080}")
    private String sidecarNode;

    @Bean
    public KarClient karClient() {
        return new KarClient(dotHost + ":" + dotRpcPort);
    }

    @Bean
    public KarSidecarClient karSidecarClient() {
        return new KarSidecarClient(sidecarNode);
    }
}
