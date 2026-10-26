package com.atoken.update.tomo.jobhandler;

import com.atoken.update.tomo.core.TomoUnbondedExpireCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("tomoUnbondedExpireSyncJob")
public class TomoUnbondedExpireSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private TomoUnbondedExpireCore tomoUnbondedExpireCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("tomo unbonded expire job start");
        tomoUnbondedExpireCore.unbondedExpireHandler();
        logger.info("tomo unbonded expire job end");
        ReturnT<String> returnT = new ReturnT<>(" tomo unbonded expire job success");
        returnT.setMsg("tomo unbonded expire job success");
        return returnT;
    }
}
