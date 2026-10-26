package com.atoken.update.dot.core;

import com.atoken.update.core.BlockInfoCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.service.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("dotBlockInfoCore")
public class DotBlockInfoCore extends BlockInfoCore {
    @Autowired
    public DotBlockInfoCore(@Qualifier("dotSidecarBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("dotBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }
}
