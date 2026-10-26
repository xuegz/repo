package com.atoken.update.ont.jobhandler;

import com.atoken.update.core.ont.OntBlockInfoCore;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobHandler(value = "ontBlockInfoSyncJobHandler")
public class OntBlockInfoSyncJob extends IJobHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OntBlockInfoCore ontBlockInfoCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("ont block sync start");
        ontBlockInfoCore.blockInfoHandler();
        logger.info("ont block sync end");
        ReturnT<String> returnT = new ReturnT<>("ont block sync success");
        returnT.setMsg("ont block sync success");
        return returnT;
    }
}
