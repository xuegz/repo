package com.atoken.update.ont.jobhandler;

import com.atoken.update.core.ont.OntBlockTxCore;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@JobHandler("ontBlockTxSyncJobHandler")
public class OntBlockTxSyncJob extends IJobHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OntBlockTxCore ontBlockTxCore;

    @Override
    public ReturnT<String> execute(String s) {
        logger.info("ont block tx sync start");
        ontBlockTxCore.blockTxHandler();
        logger.info("ont block tx sync end");
        ReturnT<String> returnT = new ReturnT<>("ont block tx sync success");
        returnT.setMsg("ont block tx sync success");
        return returnT;
    }
}
