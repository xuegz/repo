package com.atoken.update.dot.core;

import com.atoken.update.entity.Address;
import com.atoken.update.service.BlockHandlerService;
import com.atoken.update.service.dot.DotService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class DotDelegateInfoCore implements AtokenLogger {
    @Autowired
    private DotService dotService;

    @Autowired
    @Qualifier("dotSidecarBlockHandlerService")
    private BlockHandlerService blockHandlerService;

    public void delegateInfoHandler() {
        // 获取有质押地址的列表
        List<Address> addressList = dotService.selectStakedAddresses();
    }
}
