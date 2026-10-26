package com.atoken.update.kusama.jobhandler;

import com.atoken.update.dot.jobhandler.DotDelegateInfoSyncJob;
import com.atoken.update.kusama.core.KsmDelegateInfoCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("ksmDelegateInfoSyncJob")
public class KsmDelegateInfoSyncJob extends IJobHandler implements AtokenLogger {

    @Autowired
    private KsmDelegateInfoCore ksmDelegateInfoCore;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("ksm update delegate info job start");
        ksmDelegateInfoCore.delegateInfoHandler();
        logger.info("ksm update delegate info job end");
        ReturnT<String> returnT = new ReturnT<>(" ksm update delegate info job success");
        returnT.setMsg("ksm update delegate info job success");
        return returnT;
    }
}
