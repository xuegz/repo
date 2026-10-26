package com.atoken.update.atom.jobhandler;

import com.atoken.update.atom.core.AtomUnbondedExpireCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
//@JobHandler("atomUnbondedExpireSyncJob")
public class AtomUnbondedExpireSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private AtomUnbondedExpireCore atomUnbondedExpireCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("atom unbonded expire job start");
        atomUnbondedExpireCore.unbondedExpireHandler();
        logger.info("atom unbonded expire job end");
        ReturnT<String> returnT = new ReturnT<>(" atom unbonded expire job success");
        returnT.setMsg("atom unbonded expire job success");
        return returnT;
    }
}
