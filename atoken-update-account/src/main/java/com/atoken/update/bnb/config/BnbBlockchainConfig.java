package com.atoken.update.bnb.config;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.bnb")
public class BnbBlockchainConfig extends BlockchainConfig {
}
