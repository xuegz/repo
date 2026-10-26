package com.atoken.update.kusama.jobhandler;

import com.atoken.update.dot.jobhandler.DotValidatorInfoSyncJob;
import com.atoken.update.kusama.core.KsmValidatorInfoCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ksmValidatorInfoSyncJobHandler")
public class KsmValidatorInfoSyncJob extends IJobHandler implements AtokenLogger {

    @Autowired
    private KsmValidatorInfoCore ksmValidatorInfoCore;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("ksm validators job start");
        ksmValidatorInfoCore.validatorHandler();
        logger.info("ksm validators job end");
        ReturnT<String> returnT = new ReturnT<>(" ksm validators job success");
        returnT.setMsg("ksm validators job success");
        return returnT;
    }
}
