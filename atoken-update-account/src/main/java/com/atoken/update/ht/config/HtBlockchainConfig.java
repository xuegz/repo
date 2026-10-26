package com.atoken.update.ht.config;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.ht")
public class HtBlockchainConfig extends BlockchainConfig {
}
