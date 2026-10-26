package com.atoken.update.tomo.core;

import com.atoken.blockchain.http.request.account.tomo.*;
import com.atoken.blockchain.http.request.enums.CoinEnum;
import com.atoken.blockchain.utils.DataUtil;
import com.atoken.update.entity.coin.account.staking.ValidatorEO;
import com.atoken.update.service.coin.account.AccountStakingService;
import com.atoken.update.util.AtokenLogger;
import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TomoValidatorInfoCore implements AtokenLogger {
    @Autowired
    private AccountStakingService stakingService;
    @Autowired
    private TomoClient tomoClient;
    @Value("${blockchain.atom.imgUploadUrl}")
    private String imgUploadUrl;
    public static final String TABLE_PREFIX = "tomo";


    public void validatorHandler() {
        logger.info("tomo 验证节点同步开始");

        // 获取数据库绑定的验证人列表
        List<ValidatorEO> dbBondedValidators = stakingService.selectBondedValidators(TABLE_PREFIX);
        Map<String, ValidatorEO> validatorEOMap = dbBondedValidators.stream().collect(Collectors.toMap(ValidatorEO::getAddr, Functions.identity()));

        // 获取节点绑定的验证人列表
        TomoMasterNodes tomoMasterNodes = tomoClient.getMasterNodes();

        Map<String, TomoMasterNode> tomoMasterNodeMap = tomoMasterNodes.getItems().stream().filter(TomoMasterNode::getIsMasternode).collect(Collectors.toMap(TomoMasterNode::getCandidate, Functions.identity()));

        // 找到节点和数据库状态不一致的验证人
        Set<String> nodeBondedValidatorAddresses = tomoMasterNodeMap.keySet();
        List<ValidatorEO> unbondValidators = dbBondedValidators.stream().filter(dbBondedValidator -> !nodeBondedValidatorAddresses.contains(dbBondedValidator.getAddr())).collect(Collectors.toList());

        // 处理绑定的验证人
        tomoMasterNodeMap.entrySet().forEach(tomoMasterNode -> {
            try{
                TomoMasterNode masterNode = tomoMasterNode.getValue();
                // 计算年华收益率
                double yearYield = 0d;
                yearYield = tomoClient.geYearYieldBy(masterNode.getCandidate());

                // 获取验证人的委托人数量
                List<Address> tomoDelegators = tomoClient.getVoters(masterNode.getCandidate());
                int allDelegatorNum = tomoDelegators.size();

                ValidatorEO validatorEO = validatorEOMap.get(masterNode.getCandidate());
                if (Objects.isNull(validatorEO)) {
                    validatorEO = stakingService.selectValidatorByAddress(TABLE_PREFIX, masterNode.getCandidate());
                }

                if (Objects.isNull(validatorEO)) {
                    String headerImg = "http://app.biliangwang.com/icon_TOMO.png";

                    ValidatorEO validator = structureValidator(masterNode.getCandidate(), headerImg, allDelegatorNum, yearYield, TomoValidatorStatusEnum.BOUNDED, masterNode.getCapacityNumber(), masterNode.getName());
                    stakingService.insertValidator(TABLE_PREFIX, validator);
                } else {
                    String headerImg = "http://app.biliangwang.com/icon_TOMO.png";
                    ValidatorEO validator = structureValidator(masterNode.getCandidate(), headerImg, allDelegatorNum, yearYield, TomoValidatorStatusEnum.BOUNDED, masterNode.getCapacityNumber(), validatorEO.getMoniker());
                    stakingService.updateValidatorByAddress(TABLE_PREFIX, validator);
                }
            }catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        });

        // 处理正在解绑的验证人
        unbondValidators.forEach(unbondValidator -> {
            try{
                // 计算年华收益率
                Double yearYield = 0d;
                yearYield = tomoClient.geYearYieldBy(unbondValidator.getAddr());

                // 获取验证人的委托人数量
                List<Address> delegators = tomoClient.getVoters(unbondValidator.getAddr());
                Integer allDelegatorNum = delegators.size();


                ValidatorEO validator = structureValidator(unbondValidator.getAddr(), unbondValidator.getKeybaseUrl(), allDelegatorNum, yearYield, TomoValidatorStatusEnum.BOUNDED, BigDecimal.ZERO, unbondValidator.getMoniker());
                stakingService.updateValidatorByAddress(TABLE_PREFIX, validator);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        });
        logger.info("tomo 验证节点同步结束");
    }
//
//    public void validatorHandler() {
//        logger.info("tomo 验证节点同步开始");
//
//        // 获取数据库绑定的验证人列表
//        List<ValidatorEO> dbBondedValidators = stakingService.selectBondedValidators(TABLE_PREFIX);
//        Map<String, ValidatorEO> validatorEOMap = dbBondedValidators.stream().collect(Collectors.toMap(ValidatorEO::getAddr, Functions.identity()));
//
//        // 获取节点绑定的验证人列表
//        List<Address> nodeBondedValidators = tomoClient.getValidators();
//
//        // 找到节点和数据库状态不一致的验证人
//        List<String> nodeBondedValidatorAddresses = nodeBondedValidators.stream().map(nodeBondedValidator -> nodeBondedValidator.getValue()).collect(Collectors.toList());
//        List<ValidatorEO> unbondValidators = dbBondedValidators.stream().filter(dbBondedValidator -> !nodeBondedValidatorAddresses.contains(dbBondedValidator.getAddr())).collect(Collectors.toList());
//
//        // 处理绑定的验证人
//        nodeBondedValidators.forEach(nodeBondedValidator -> {
//            try{
//                // 计算年华收益率
//                Double yearYield = 0d;
//                yearYield = tomoClient.geYearYieldBy(nodeBondedValidator.getValue());
//
//                // 获取验证人的委托人数量
//                List<Address> tomoDelegators = tomoClient.getVoters(nodeBondedValidator.getValue());
//                Integer allDelegatorNum = tomoDelegators.size();
//
//                ValidatorEO validatorEO = validatorEOMap.get(nodeBondedValidator.getValue());
//                if (Objects.isNull(validatorEO)) {
//                    validatorEO = stakingService.selectValidatorByAddress(TABLE_PREFIX, nodeBondedValidator.getValue());
//                }
//
//                BigDecimal token = DataUtil.formatAmount(tomoClient.getCandidateCap(nodeBondedValidator.getValue()), CoinEnum.tomo.decimals());
//
//                if (Objects.isNull(validatorEO)) {
//                    String headerImg = "";
//
//                    Address onwerAddress = tomoClient.getValidatorOwner(nodeBondedValidator.getValue());
//                    TomoAccount tomoAccount = tomoClient.getAccountInfo(onwerAddress.getValue());
//                    ValidatorEO validator = structureValidator(nodeBondedValidator.getValue(), headerImg, allDelegatorNum, yearYield, TomoValidatorStatusEnum.BOUNDED, token, tomoAccount.getAccountName());
//                    stakingService.insertValidator(TABLE_PREFIX, validator);
//                } else {
//                    String headerImg = "";
//                    ValidatorEO validator = structureValidator(nodeBondedValidator.getValue(), headerImg, allDelegatorNum, yearYield, TomoValidatorStatusEnum.BOUNDED, token, validatorEO.getMoniker());
//                    stakingService.updateValidatorByAddress(TABLE_PREFIX, validator);
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//                logger.error(e.getLocalizedMessage());
//            }
//        });
//
//        // 处理正在解绑的验证人
//        unbondValidators.forEach(unbondValidator -> {
//            try{
//                // 计算年华收益率
//                Double yearYield = 0d;
//                yearYield = tomoClient.geYearYieldBy(unbondValidator.getAddr());
//
//                // 获取验证人的委托人数量
//                List<Address> delegators = tomoClient.getVoters(unbondValidator.getAddr());
//                Integer allDelegatorNum = delegators.size();
//
//
//                ValidatorEO validator = structureValidator(unbondValidator.getAddr(), unbondValidator.getKeybaseUrl(), allDelegatorNum, yearYield, TomoValidatorStatusEnum.BOUNDED, BigDecimal.ZERO, unbondValidator.getMoniker());
//                stakingService.updateValidatorByAddress(TABLE_PREFIX, validator);
//            }catch (Exception e) {
//                e.printStackTrace();
//                logger.error(e.getLocalizedMessage());
//            }
//        });
//        logger.info("tomo 验证节点同步结束");
//    }

    private ValidatorEO structureValidator(String address, String headerImg, Integer allDelegatorNum, Double yearYield, TomoValidatorStatusEnum tomoValidatorStatusEnum, BigDecimal token, String moniker) {
        ValidatorEO validatorEO = new ValidatorEO();
        validatorEO.setAddr(address);
        validatorEO.setConsensusPubkey("");
        validatorEO.setAllDelegatorNum(allDelegatorNum);
        validatorEO.setDelegatorShares(token);
        validatorEO.setDetails(null);
        validatorEO.setIdentity(null);
        validatorEO.setJailed((short) 0);
        validatorEO.setKeybaseUrl(headerImg);
        validatorEO.setMaxChangeRate(null);
        validatorEO.setMaxRate(null);
        validatorEO.setRate(null);
        validatorEO.setMoniker(moniker);
        validatorEO.setStatus(tomoValidatorStatusEnum.status());
        validatorEO.setTokens(token);
        validatorEO.setUnbondingHeight(0);
        validatorEO.setUnbondingTimestamp(0L);
        validatorEO.setWebsite("");
        validatorEO.setYearYield(yearYield);
        validatorEO.setRateUpdateTimestamp(0L);
        return validatorEO;
    }

}
