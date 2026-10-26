package com.atoken.update.kusama.core;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.update.dot.core.DotPushNominateCore;
import com.atoken.update.entity.Tx;
import com.atoken.update.entity.coin.account.ksm.KsmNominateExtrinsic;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.service.coin.account.ksm.KsmService;
import com.atoken.update.util.AtokenLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KsmPushNominateCore implements AtokenLogger {

    @Autowired
    private KsmService ksmService;

    @Autowired
    private KsmClient ksmClient;

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    public void pushNominateHander() {
        // 获取待处理的提名交易列表
        List<KsmNominateExtrinsic> dotNominateExtrinsics = this.ksmService.selectNominateExtrinsic(ksmBlockchainConfig.getTablePrefix());
        dotNominateExtrinsics.forEach(dotNominateExtrinsic -> {
            try {
                // 通过hash获取绑定交易
                Tx tx = this.ksmService.selectTxByHash(ksmBlockchainConfig.getTablePrefix(),dotNominateExtrinsic.getBondHash());
                if (tx.getStatus() == 1 || tx.getStatus() == 2) {
                    if (tx.getState() == 1) {
                        // 如果绑定交易成功则发起提名交易
                        try {
                            String hash = this.ksmClient.sendTx(dotNominateExtrinsic.getExtrinsic());
                            dotNominateExtrinsic.setHash(hash);
                            dotNominateExtrinsic.setStatus("PROCESSED");
                            this.ksmService.updateExtrinsicStatus(this.ksmBlockchainConfig.getTablePrefix(),dotNominateExtrinsic);
                        } catch (Exception e) {
                            logger.error(e.getLocalizedMessage());
                            dotNominateExtrinsic.setStatus("FAILURE");
                            dotNominateExtrinsic.setErrorMessage(e.getLocalizedMessage());
                            ksmService.updateExtrinsicStatus(this.ksmBlockchainConfig.getTablePrefix(),dotNominateExtrinsic);
                        }
                    } else {
                        // 如果失败则更新状态
                        dotNominateExtrinsic.setStatus("FAILURE");
                        dotNominateExtrinsic.setErrorMessage("bond tx failure");
                        ksmService.updateExtrinsicStatus(this.ksmBlockchainConfig.getTablePrefix(),dotNominateExtrinsic);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        });
    }
}
