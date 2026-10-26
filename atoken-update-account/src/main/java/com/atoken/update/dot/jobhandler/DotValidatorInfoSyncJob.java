package com.atoken.update.dot.jobhandler;

import com.atoken.update.dot.core.DotValidatorInfoCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("dotValidatorInfoSyncJobHandler")
public class DotValidatorInfoSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private DotValidatorInfoCore dotValidatorInfoCore;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("dot validators job start");

        try {
            dotValidatorInfoCore.validatorHandler();
        } catch (Exception e) {
            return ReturnT.FAIL;
        }
        logger.info("dot validators job end");
        ReturnT<String> returnT = new ReturnT<>(" dot validators job success");
        returnT.setMsg("dot validators job success");
        return returnT;
    }
}
