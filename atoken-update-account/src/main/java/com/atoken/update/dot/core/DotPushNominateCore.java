package com.atoken.update.dot.core;

import com.atoken.blockchain.http.request.account.dot.DotClient;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.dot.DotNominateExtrinsic;
import com.atoken.update.service.dot.DotService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用 组合发送模式 这个任务不需要了
 */
@Deprecated
@Service
public class DotPushNominateCore implements AtokenLogger {
    @Autowired
    private DotService dotService;
    @Autowired
    private DotClient dotClient;

    public void pushNominateHander() {
        // 获取待处理的提名交易列表
        List<DotNominateExtrinsic> dotNominateExtrinsics = dotService.selectNominateExtrinsic();
        dotNominateExtrinsics.forEach(dotNominateExtrinsic -> {
            try {
                // 通过hash获取绑定交易
                Tx tx = dotService.selectTxByHash(dotNominateExtrinsic.getBondHash());
                if (tx.getStatus() == 1 || tx.getStatus() == 2) {
                    if (tx.getState() == 1) {
                        // 如果绑定交易成功则发起提名交易
                        try {
                            String hash = dotClient.sendTx(dotNominateExtrinsic.getExtrinsic());
                            dotNominateExtrinsic.setHash(hash);
                            dotNominateExtrinsic.setStatus("PROCESSED");
                            dotService.updateExtrinsicStatus(dotNominateExtrinsic);
                        } catch (Exception e) {
                            logger.error(e.getLocalizedMessage());
                            dotNominateExtrinsic.setStatus("FAILURE");
                            dotNominateExtrinsic.setErrorMessage(e.getLocalizedMessage());
                            dotService.updateExtrinsicStatus(dotNominateExtrinsic);
                        }
                    } else {
                        // 如果失败则更新状态
                        dotNominateExtrinsic.setStatus("FAILURE");
                        dotNominateExtrinsic.setErrorMessage("bond tx failure");
                        dotService.updateExtrinsicStatus(dotNominateExtrinsic);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        });
    }
}
