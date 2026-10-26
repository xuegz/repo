package com.atoken.update.atom.core;

import com.atoken.update.core.BlockInfoCore;
import com.atoken.update.entity.BlockchainConfig;
import com.atoken.update.service.BlockHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

//@Service("atomBlockInfoCore")
public class AtomBlockInfoCore extends BlockInfoCore {
    @Autowired
    public AtomBlockInfoCore(@Qualifier("atomBlockHandlerService") BlockHandlerService blockHandlerService, @Qualifier("atomBlockchainConfig") BlockchainConfig blockchainConfig) {
        super(blockHandlerService, blockchainConfig);
    }
}
