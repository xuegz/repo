package com.atoken.update.dot.config;

import com.atoken.update.entity.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.dot")
public class DotBlockchainConfig extends BlockchainConfig {
}
