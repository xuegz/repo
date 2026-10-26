package com.atoken.update.kusama.jobhandler;

import com.atoken.update.dot.core.DotUnbondedExpireCore;
import com.atoken.update.dot.jobhandler.DotUnbondedExpireSyncJob;
import com.atoken.update.kusama.core.KsmUnbondedExpireCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ksmUnbondedExpireSyncJob")
public class KsmUnbondedExpireSyncJob extends IJobHandler implements AtokenLogger {

    @Autowired
    private KsmUnbondedExpireCore ksmUnbondedExpireCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("ksm unbonded expire job start");
        ksmUnbondedExpireCore.unbondedExpireHandler();
        logger.info("ksm unbonded expire job end");
        ReturnT<String> returnT = new ReturnT<>(" dot unbonded expire job success");
        returnT.setMsg("ksm unbonded expire job success");
        return returnT;
    }
}
