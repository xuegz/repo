package com.atoken.update.kusama.core;

import com.atoken.blockchain.http.request.account.ksm.KsmClient;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.dot.core.DotValidatorInfoCore;
import com.atoken.update.entity.Final;
import com.atoken.update.entity.coin.account.ksm.KsmValidator;
import com.atoken.update.kusama.config.KsmBlockchainConfig;
import com.atoken.update.service.coin.account.ksm.KsmService;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.type.AccountId;
import org.polkadot.types.type.Exposure;
import org.polkadot.types.type.ValidatorPrefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KsmValidatorInfoCore implements AtokenLogger {

    @Autowired
    private KsmService ksmService;

    @Autowired
    private KsmClient ksmClient;

    @Autowired
    private KsmBlockchainConfig ksmBlockchainConfig;

    public void validatorHandler() {
        logger.info("ksm 验证节点同步开始");

        // 查询数据库支持的验证人列表
        List<KsmValidator> dbBondedValidators = ksmService.selectBondedValidators(ksmBlockchainConfig.getTablePrefix());
        Map<String, KsmValidator> dbBondedValidatorMap = new HashMap<>();
        dbBondedValidators.forEach(dotValidator -> dbBondedValidatorMap.put(dotValidator.getAddr(), dotValidator));

        // 获取验证人地址
        Vector<AccountId> validators = this.ksmClient.getValidators();

        // 获取总发行量
        BigDecimal totalIssuance = this.ksmClient.getTotalIssuance();

        // 计算质押人数，质押量，获取验证人信息
        Map<String, KsmValidatorInfoCore.ValidatorInfo> validatorInfoMap = new HashMap<>();
        Final<Integer> allDelegatorNum = Final.newFinal(0);
        Final<BigDecimal> totalDelegateAmount = Final.newFinal(BigDecimal.ZERO);
        validators.forEach(validator -> {
            // 从链上获取验证人信息
            ValidatorPrefs validatorPrefs = this.ksmClient.getValidatorInfoByValidatorAddr(validator.toString());
            //从链上获取验证人的质押人信息
            Exposure exposure = this.ksmClient.getStakersByValidatorAddr(validator.toString());
            allDelegatorNum.setType(allDelegatorNum.getType() + exposure.getOthers().size());
            totalDelegateAmount.setType(totalDelegateAmount.getType().add(new BigDecimal(exposure.getTotal().toBn())));
            validatorInfoMap.put(validator.toString(), new KsmValidatorInfoCore.ValidatorInfo(validatorPrefs, exposure));
        });

        // 处理未解绑的验证人
        List<KsmValidator> unbondedValidators = dbBondedValidators.stream().filter(dbBondedValidator -> !validatorInfoMap.containsKey(dbBondedValidator.getAddr())).collect(Collectors.toList());
        validatorInfoMap.entrySet().forEach(validatorInfo -> {
            // 查询库里的质押节点信息
            KsmValidator dotValidator = this.ksmService.selectValidatorByAddress(ksmBlockchainConfig.getTablePrefix(),validatorInfo.getKey());
            // 计算年化
            BigDecimal payment = new BigDecimal(validatorInfo.getValue().getValidatorPrefs().getValidatorPayment().toBn());
            Double annualReturn = (totalIssuance.doubleValue() * 0.01 / validators.size() - payment.doubleValue() * 365 * 24) / validatorInfo.getValue().getExposure().getTotal().doubleValue();
            Double commissionRate = payment.doubleValue() / totalDelegateAmount.getType().doubleValue();
            if (Objects.isNull(dotValidator)) {
                // 不在我们库中就插入
                KsmValidator validator = new KsmValidator();
                validator.setAddr(validatorInfo.getKey());
                validator.setAllDelegatorNum(validatorInfo.getValue().getExposure().getOthers().size());
                validator.setAnnualReturn(annualReturn < 0 ? 0 : annualReturn);
                validator.setCommissionRate(commissionRate);
                validator.setStatus((short) ConstantUtil.DOT_VALIDATOR_STATUS_BONDED);
                validator.setTokens(DataUtil.formatAmount(new BigDecimal(validatorInfo.getValue().getExposure().getTotal().toBn()), CoinEnum.ksm.decimals()));
                this.ksmService.insertValidator(ksmBlockchainConfig.getTablePrefix(),validator);
            } else {
                // 在我们库里就更新
                dotValidator.setAddr(validatorInfo.getKey());
                dotValidator.setAllDelegatorNum(validatorInfo.getValue().getExposure().getOthers().size());
                dotValidator.setAnnualReturn(annualReturn < 0 ? 0 : annualReturn);
                dotValidator.setCommissionRate(commissionRate);
                dotValidator.setStatus((short) ConstantUtil.DOT_VALIDATOR_STATUS_BONDED);
                this.ksmService.updateValidatorByAddr(ksmBlockchainConfig.getTablePrefix(),dotValidator);
            }
        });

        // 更新已经解绑的质押人
        unbondedValidators.forEach(unbondedValidator -> {
            unbondedValidator.setStatus((short) 0);
            this.ksmService.updateValidatorByAddr(ksmBlockchainConfig.getTablePrefix(),unbondedValidator);
        });

        logger.info("ksm 验证节点同步完成");
    }

    class ValidatorInfo {
        private ValidatorPrefs validatorPrefs;
        private Exposure exposure;

        public ValidatorInfo(ValidatorPrefs validatorPrefs, Exposure exposure) {
            this.validatorPrefs = validatorPrefs;
            this.exposure = exposure;
        }

        public ValidatorPrefs getValidatorPrefs() {
            return validatorPrefs;
        }

        public void setValidatorPrefs(ValidatorPrefs validatorPrefs) {
            this.validatorPrefs = validatorPrefs;
        }

        public Exposure getExposure() {
            return exposure;
        }

        public void setExposure(Exposure exposure) {
            this.exposure = exposure;
        }
    }
}
