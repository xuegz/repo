package com.atoken.update.dot.jobhandler;

import com.atoken.update.dot.core.DotDelegateInfoCore;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler("dotDelegateInfoSyncJob")
public class DotDelegateInfoSyncJob extends IJobHandler implements AtokenLogger {
    @Autowired
    private DotDelegateInfoCore dotDelegateInfoCore;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("dot update delegate info job start");
        dotDelegateInfoCore.delegateInfoHandler();
        logger.info("dot update delegate info job end");
        ReturnT<String> returnT = new ReturnT<>(" dot update delegate info job success");
        returnT.setMsg("dot update delegate info job success");
        return returnT;
    }
}
