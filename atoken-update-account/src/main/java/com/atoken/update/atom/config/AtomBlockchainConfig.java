package com.atoken.update.atom.config;

import com.atoken.update.entity.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.atom")
public class AtomBlockchainConfig extends BlockchainConfig {
}
