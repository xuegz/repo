package com.atoken.update.tomo.jobhandler;

import com.atoken.update.tomo.core.TomoValidatorInfoCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("tomoValidatorInfoSyncJob")
public class TomoValidatorInfoSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private TomoValidatorInfoCore tomoValidatorInfoCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("tomo validators job start");
        tomoValidatorInfoCore.validatorHandler();
        logger.info("tomo validators job end");
        ReturnT<String> returnT = new ReturnT<>(" tomo validators job success");
        returnT.setMsg("tomo validators job success");
        return returnT;
    }
}
