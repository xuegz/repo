package com.atoken.update.dot.config;

import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotBeanConfig {
    @Value("${blockchain.dot.host}")
    private String dotHost;
    @Value("${blockchain.dot.rpc-port}")
    private String dotRpcPort;

    @Value("${rpc.dot.url.sidecar_node:http://39.96.194.39:18080}")
    private String sidecarNode;

    @Bean
    public DotClient dotClient() {
        return new DotClient(dotHost + ":" + dotRpcPort);
    }

    @Bean
    public DotSidecarClient dotSidecarClient() {
        return new DotSidecarClient(sidecarNode);
    }
}
