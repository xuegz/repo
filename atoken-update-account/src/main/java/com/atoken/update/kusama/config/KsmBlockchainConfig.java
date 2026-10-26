package com.atoken.update.kusama.config;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.ksm")
public class KsmBlockchainConfig extends BlockchainConfig {
}
