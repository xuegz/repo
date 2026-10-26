package com.atoken.update.tomo.config;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.tomo")
public class TomoBlockchainConfig extends BlockchainConfig {
}
