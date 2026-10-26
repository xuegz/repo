package com.atoken.update.jobhandler.coin.account;

import com.atoken.update.core.coin.account.BlockInfoCore;
import com.atoken.update.entity.coin.account.BlockchainConfig;
import com.atoken.update.util.AtokenLogger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BlockUpdaterJob extends IJobHandler implements AtokenLogger {

    private BlockInfoCore blockInfoCore;
    private BlockchainConfig blockchainConfig;

    public BlockUpdaterJob(BlockInfoCore blockInfoCore, BlockchainConfig blockchainConfig) {
        this.blockInfoCore = blockInfoCore;
        this.blockchainConfig = blockchainConfig;
    }


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info(" {} BlockUpdaterJob sync start", blockchainConfig.getTablePrefix());
        if (StringUtils.isEmpty(param)) {
            return ReturnT.FAIL;
        }
        log.info("-----BlockUpdaterJob start with param: {}-----", param);

        String[] split = param.split(",");
        List<Integer> blockNumberList = Arrays.stream(split).map(Integer::valueOf).collect(Collectors.toList());
        blockInfoCore.blockInfoHandler(blockNumberList);

        log.info("{} BlockUpdaterJob sync end", blockchainConfig.getMainAsset());
        ReturnT<String> returnT = new ReturnT<>(blockchainConfig.getMainAsset() + " block sync success");
        returnT.setMsg(blockchainConfig.getMainAsset() + " block sync success");
        return returnT;
    }

}
