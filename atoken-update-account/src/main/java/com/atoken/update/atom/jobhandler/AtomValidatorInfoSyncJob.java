package com.atoken.update.atom.jobhandler;

import com.atoken.update.atom.core.AtomValidatorInfoCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
//@JobHandler("atomValidatorInfoSyncJob")
public class AtomValidatorInfoSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private AtomValidatorInfoCore atomValidatorInfoCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("atom validators job start");
        atomValidatorInfoCore.validatorHandler();
        logger.info("atom validators job end");
        ReturnT<String> returnT = new ReturnT<>(" atom validators job success");
        returnT.setMsg("atom validators job success");
        return returnT;
    }
}
