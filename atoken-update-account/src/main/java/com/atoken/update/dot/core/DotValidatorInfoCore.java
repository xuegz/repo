package com.atoken.update.dot.core;

import com.atoken.blockchain.http.request.account.dot.sidecar.DotSidecarClient;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.Commission;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.Exposure;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.IndividualExposure;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.PalletStakingProgress;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.dot.DotValidator;
import com.atoken.update.service.dot.DotService;
import com.atoken.update.util.AtokenLogger;
import com.atoken.update.util.ConstantUtil;
import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_DOWN;

@Service
public class DotValidatorInfoCore implements AtokenLogger {
    @Autowired
    private DotService dotService;

    @Autowired
    private DotSidecarClient dotSidecarClient;


    public void validatorHandler() {
        logger.info("dot 验证节点同步开始");

        // 查询数据库支持的验证人列表
        List<DotValidator> dbBondedValidators = dotService.selectBondedValidators();

        Map<String, DotValidator> dbBondedValidatorMap = dbBondedValidators.stream()
                .collect(Collectors.toMap(DotValidator::getAddr, Functions.identity()));


        // 获取验证人地址
        PalletStakingProgress palletStakingProgress = dotSidecarClient.palletsStakingProgress();
        // 获取总发行量 单位dot
        BigDecimal totalIssuance = DataUtil.formatAmount(dotSidecarClient.totalIssuance(), 10);
        // 验证人节点数量
        int idealValidatorCount = Integer.parseInt(palletStakingProgress.getIdealValidatorCount());


        IndividualExposure individualExposure = dotSidecarClient.stakingErasRewardPoints();


        palletStakingProgress.getValidatorSet().parallelStream().forEach(validator -> {
            // 从链上获取验证人信息
            Exposure exposure = dotSidecarClient.stakingErasStakers(validator);

            // 节点总质押金额 单位dot
            BigDecimal tokens = DataUtil.formatAmount(new BigDecimal(exposure.getTotal()), 10);
            // 节点委托人数量
            Integer allDelegatorNum = exposure.getOthers().size();

            // 获取佣金比率
            Commission commission = dotSidecarClient.stakingCommission(validator);
            BigDecimal commissionRate = DataUtil.formatAmount(new BigDecimal(commission.getCommission()), 9);

            // 年华收益 = 总供应量(dot) * 通胀率 / 节点数 / 365 * (1-佣金(百分比)) / 节点总质押数量(dot) * 1000
            // 1062093324.0620770618 * 0.0899 / 297 / 365 * （1 - 0.1）/ 4246262.7920595344 * 1000

            BigDecimal annualReturn = BigDecimal.ZERO;
            if (commissionRate.compareTo(BigDecimal.ONE) < 0) {
                annualReturn = totalIssuance.multiply(BigDecimal.valueOf(0.0899))
                        .divide(BigDecimal.valueOf(idealValidatorCount), 12, ROUND_DOWN)
                        .divide(BigDecimal.valueOf(365), 12, ROUND_DOWN)
                        .multiply(BigDecimal.ONE.subtract(commissionRate))
                        .divide(tokens, 12, ROUND_DOWN)
                        .multiply(BigDecimal.valueOf(1000));
            }


            int points = individualExposure.getIndividual().getOrDefault(validator, 0);

            // 查询库里的质押节点信息
            DotValidator dotValidator = dotService.selectValidatorByAddress(validator);

            if (Objects.isNull(dotValidator)) {
                // 不在我们库中就插入
                DotValidator validatorSave = new DotValidator();
                validatorSave.setAddr(validator);
                validatorSave.setAllDelegatorNum(allDelegatorNum);
                validatorSave.setAnnualReturn(annualReturn.doubleValue() < 0 ? 0 : annualReturn.doubleValue());
                validatorSave.setCommissionRate(commissionRate.doubleValue());
                validatorSave.setStatus((short) ConstantUtil.DOT_VALIDATOR_STATUS_BONDED);
                validatorSave.setTokens(tokens);
                validatorSave.setPoints(points);
                dotService.insertValidator(validatorSave);
            } else {
                // 在我们库里就更新
                dotValidator.setAddr(validator);
                dotValidator.setAllDelegatorNum(allDelegatorNum);
                dotValidator.setAnnualReturn(annualReturn.doubleValue() < 0 ? 0 : annualReturn.doubleValue());
                dotValidator.setCommissionRate(commissionRate.doubleValue());
                dotValidator.setStatus((short) ConstantUtil.DOT_VALIDATOR_STATUS_BONDED);
                dotValidator.setPoints(points);
                dotService.updateValidatorByAddr(dotValidator);
            }
        });

        // 处理未解绑的验证人
        List<DotValidator> unbondedValidators = dbBondedValidators.stream()
                .filter(dbBondedValidator -> !palletStakingProgress.getValidatorSet().contains(dbBondedValidator.getAddr()))
                .collect(Collectors.toList());


        // 更新已经解绑的质押人
        unbondedValidators.forEach(unbondedValidator -> {
            unbondedValidator.setStatus((short) 0);
            dotService.updateValidatorByAddr(unbondedValidator);
        });

        logger.info("dot 验证节点同步完成");
    }

}
