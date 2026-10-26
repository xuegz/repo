package com.atoken.update.ont.config;

import com.atoken.blockchain.http.request.account.ont.OntClient;
import com.atoken.update.entity.ont.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class OntBeanConfig {
    @Value("${blockchain.request.url: http://localhost:20336}")
    private String requestUrl;

    @Bean
    public OntClient ontClient() {
        return new OntClient(requestUrl);
    }

    @Bean
    @Qualifier("tokenMap")
    public Map<String, Token> token() {
        return new ConcurrentHashMap<>();
    }
}
