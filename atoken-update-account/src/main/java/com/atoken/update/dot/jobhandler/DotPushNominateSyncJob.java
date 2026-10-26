package com.atoken.update.dot.jobhandler;

import com.atoken.update.dot.core.DotPushNominateCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("dotPushNominateSyncJob")
public class DotPushNominateSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private DotPushNominateCore dotPushNominateCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("dot push nominate job start");
        dotPushNominateCore.pushNominateHander();
        logger.info("dot push nominate job end");
        ReturnT<String> returnT = new ReturnT<>(" dot push nominate job success");
        returnT.setMsg("dot push nominate job success");
        return returnT;
    }
}
