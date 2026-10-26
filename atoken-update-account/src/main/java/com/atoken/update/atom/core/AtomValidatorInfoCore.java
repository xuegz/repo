package com.atoken.update.atom.core;

import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.account.atom.*;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.blockchain.utils.Final;
import com.atoken.update.entity.atom.AtomValidatorEO;
import com.atoken.update.service.atom.AtomService;
import com.atoken.update.util.AtokenLogger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//@Service
public class AtomValidatorInfoCore implements AtokenLogger {
    @Autowired
    private AtomService atomService;
    @Autowired
    private AtomClient atomClient;
    @Value("${blockchain.atom.imgUploadUrl}")
    private String imgUploadUrl;

    public void validatorHandler() {
        logger.info("atom 验证节点同步开始");

        // 获取数据库绑定的验证人列表
        List<AtomValidatorEO> dbBondedValidators = atomService.selectBondedValidators();
        Map<String, AtomValidatorEO> atomValidatorEOMap = new HashMap<>();
        dbBondedValidators.forEach(dbBondedValidator -> atomValidatorEOMap.put(dbBondedValidator.getAddr(), dbBondedValidator));

        // 获取节点绑定的验证人列表
        List<AtomValidator> nodeBondedValidators = atomClient.getAtomValidators();

        // 找到节点和数据库状态不一致的验证人
        List<String> nodeBondedValidatorAddresses = nodeBondedValidators.stream().map(nodeBondedValidator -> nodeBondedValidator.getOperatorAddress()).collect(Collectors.toList());
        List<AtomValidatorEO> unbondValidators = dbBondedValidators.stream().filter(dbBondedValidator -> !nodeBondedValidatorAddresses.contains(dbBondedValidator.getAddr())).collect(Collectors.toList());

        // 计算总的质押数量
        Final<BigDecimal> allToken = Final.newFinal(BigDecimal.ZERO);
        nodeBondedValidators.forEach(atomValidator -> allToken.setType(allToken.getType().add(atomValidator.getTokens())));

        // 获取通胀率
        Double inflation = atomClient.getInflation();

        // 获取节点token信息
        AtomStakingPool atomStakingPool = atomClient.getAtomStakingPool();

        Double interestRate = inflation / (atomStakingPool.getBondedTokens().doubleValue()/(atomStakingPool.getBondedTokens().doubleValue() + atomStakingPool.getNotBondedTokens().doubleValue()));
        // 处理绑定的验证人
        nodeBondedValidators.forEach(nodeBondedValidator -> {
            try{
                // 计算年华收益率
                Double yearYield = 0d;
                yearYield = interestRate * (1 - nodeBondedValidator.getCommission().getRate());

                // 获取验证人的委托人数量
                List<AtomDelegator> atomDelegators = atomClient.getAtomDelegatorsByValidator(nodeBondedValidator.getOperatorAddress());
                Integer allDelegatorNum = atomDelegators.size();

                AtomValidatorEO atomValidatorEO = atomValidatorEOMap.get(nodeBondedValidator.getOperatorAddress());
                if (Objects.isNull(atomValidatorEO)) {
                    atomValidatorEO = atomService.selectValidatorByAddress(nodeBondedValidator.getOperatorAddress());
                }

                if (Objects.isNull(atomValidatorEO)) {
                    // 获取头像
                    String keybaseUrl = atomClient.getValidatorKeybaseUrl(nodeBondedValidator.getOperatorAddress());
                    String headerImg = "";
                    // 调用七牛服务，上传文件
                    if (Strings.isNotBlank(keybaseUrl)) {
                        headerImg = uploadImg(keybaseUrl);
                    }

                    AtomValidatorEO validatorEO = structureAtomValidator(nodeBondedValidator, headerImg, allDelegatorNum, yearYield);
                    atomService.insertValidator(validatorEO);
                } else {
                    String headerImg = atomValidatorEO.getKeybaseUrl();
                    if (Objects.isNull(headerImg)) {
                        String keybaseUrl = atomClient.getValidatorKeybaseUrl(nodeBondedValidator.getOperatorAddress());
                        // 调用七牛服务，上传文件
                        if (Strings.isNotBlank(keybaseUrl)) {
                            headerImg = uploadImg(keybaseUrl);
                        }
                    }

                    AtomValidatorEO validatorEO = structureAtomValidator(nodeBondedValidator, headerImg, allDelegatorNum, yearYield);
                    atomService.updateValidatorByAddress(validatorEO);
                }
            }catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        });

        // 处理正在解绑的验证人
        unbondValidators.forEach(unbondValidator -> {
            try{
                AtomValidator atomValidator = atomClient.getValidatorByAddress(unbondValidator.getAddr());
                // 计算年华收益率
                Double yearYield = 0d;
                yearYield = interestRate * (1 - atomValidator.getCommission().getRate());

                // 获取验证人的委托人数量
                List<AtomDelegator> atomDelegators = atomClient.getAtomDelegatorsByValidator(atomValidator.getOperatorAddress());
                Integer allDelegatorNum = atomDelegators.size();


                AtomValidatorEO validatorEO = structureAtomValidator(atomValidator, unbondValidator.getKeybaseUrl(), allDelegatorNum, yearYield);
                atomService.updateValidatorByAddress(validatorEO);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        });
        logger.info("atom 验证节点同步结束");
    }

    private AtomValidatorEO structureAtomValidator(AtomValidator atomValidator, String headerImg, Integer allDelegatorNum, Double yearYield) {
        AtomValidatorEO validatorEO = new AtomValidatorEO();
        validatorEO.setAddr(atomValidator.getOperatorAddress());
        validatorEO.setConsensusPubkey(atomValidator.getConsensusPubkey());
        validatorEO.setAllDelegatorNum(allDelegatorNum);
        validatorEO.setDelegatorShares(atomValidator.getDelegatorShares());
        validatorEO.setDetails(atomValidator.getDescription().getDetails());
        validatorEO.setIdentity(atomValidator.getDescription().getIdentity());
        validatorEO.setJailed(atomValidator.getJailed()? (short)1: 0);
        validatorEO.setKeybaseUrl(headerImg);
        validatorEO.setMaxChangeRate(atomValidator.getCommission().getMaxChangeRate());
        validatorEO.setMaxRate(atomValidator.getCommission().getMaxRate());
        validatorEO.setRate(atomValidator.getCommission().getRate());
        validatorEO.setMoniker(atomValidator.getDescription().getMoniker());
        validatorEO.setStatus(atomValidator.getStatus().shortValue());
        validatorEO.setTokens(DataUtil.formatAmount(atomValidator.getTokens(), CoinEnum.atom.decimals()));
        validatorEO.setUnbondingHeight(Integer.valueOf(atomValidator.getUnbondingHeight()));
        validatorEO.setUnbondingTimestamp(atomValidator.getUnbondingTime().getTime() / 1000);
        validatorEO.setWebsite(atomValidator.getDescription().getWebsite());
        validatorEO.setYearYield(yearYield);
        validatorEO.setRateUpdateTimestamp(atomValidator.getCommission().getUpdateTime().getTime() / 1000);
        return validatorEO;
    }

    private String uploadImg(String imgUrl) {
        try {
            UploadImgResponse uploadImgResponse = new Request().url(imgUploadUrl).data("imgUrl", imgUrl).method(Method.GET).send(UploadImgResponse.class);
            return "0".equals(uploadImgResponse.code) ?  uploadImgResponse.data: "";
        }catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }
    }

    public static class UploadImgResponse {
        private String code;
        private String message;
        private String data;
        private boolean ok;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }
    }
}
