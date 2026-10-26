package com.atoken.update.kar.config;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.kar")
public class KarBlockchainConfig extends BlockchainConfig {
}
