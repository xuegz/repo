package com.atoken.update.xwc.config;

import com.atoken.blockchain.http.request.account.xwc.XwcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author legends_bai
 * @date 2020/12/9 上午11:49
 */
@Configuration
public class XwcBeanConfig {

    @Value("${blockchain.xwc.host}")
    private String url;
    @Value("${blockchain.xwc.rpc-port}")
    private String port;

    @Bean
    public XwcClient xWcClient() {
        return new XwcClient(url,port);
    }
}
