package com.atoken.update.ont.jobhandler;

import com.atoken.update.core.ont.OntAddressBalanceCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@JobHandler(value = "ontAddressBalanceSyncJobHandler")
public class OntAddressBalanceSyncJob extends IJobHandler implements AtokenLogger{
    @Autowired
    private OntAddressBalanceCore ontAddressBalanceCore;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("ont address balance sync start");
        ontAddressBalanceCore.addressBalanceHandler();
        logger.info("ont address balance sync end");
        ReturnT<String> returnT = new ReturnT<>("ont address balance sync success");
        returnT.setMsg("ont address balance sync success");
        return returnT;
    }
}
