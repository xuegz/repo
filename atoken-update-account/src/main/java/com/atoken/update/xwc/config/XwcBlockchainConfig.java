package com.atoken.update.xwc.config;

import com.atoken.update.entity.coin.account.BlockchainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain.xwc")
public class XwcBlockchainConfig extends BlockchainConfig {
}
