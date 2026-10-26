package com.atoken.update.kusama.jobhandler;

import com.atoken.update.dot.core.DotPushNominateCore;
import com.atoken.update.dot.jobhandler.DotPushNominateSyncJob;
import com.atoken.update.kusama.core.KsmPushNominateCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ksmPushNominateSyncJob")
public class KsmPushNominateSyncJob extends IJobHandler implements AtokenLogger {

    @Autowired
    private KsmPushNominateCore ksmPushNominateCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("ksm push nominate job start");
        ksmPushNominateCore.pushNominateHander();
        logger.info("ksm push nominate job end");
        ReturnT<String> returnT = new ReturnT<>(" dot push nominate job success");
        returnT.setMsg("ksm push nominate job success");
        return returnT;
    }
}
