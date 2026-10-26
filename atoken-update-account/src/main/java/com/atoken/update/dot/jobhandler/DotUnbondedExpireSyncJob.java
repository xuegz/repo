package com.atoken.update.dot.jobhandler;

import com.atoken.update.dot.core.DotUnbondedExpireCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("dotUnbondedExpireSyncJob")
public class DotUnbondedExpireSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private DotUnbondedExpireCore dotUnbondedExpireCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("dot unbonded expire job start");
        dotUnbondedExpireCore.unbondedExpireHandler();
        logger.info("dot unbonded expire job end");
        ReturnT<String> returnT = new ReturnT<>(" dot unbonded expire job success");
        returnT.setMsg("dot unbonded expire job success");
        return returnT;
    }
}
