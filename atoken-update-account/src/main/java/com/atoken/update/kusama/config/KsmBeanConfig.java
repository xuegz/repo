package com.atoken.update.kusama.config;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.blockchain.http.request.account.ksm.sidecar.KsmSidecarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author evil
 * tip: 因为与dot一样所以client不用重新写
 */
@Configuration
public class KsmBeanConfig {

    @Value("${blockchain.ksm.host}")
    private String dotHost;
    @Value("${blockchain.ksm.rpc-port}")
    private String dotRpcPort;

    @Value("${rpc.ksm.url.sidecar_node:http://39.96.194.39:28080}")
    private String sidecarNode;

    @Bean
    public KsmClient ksmClient() {
        return new KsmClient(dotHost + ":" + dotRpcPort);
    }

    @Bean
    public KsmSidecarClient ksmSidecarClient() {
        return new KsmSidecarClient(sidecarNode);
    }
}
