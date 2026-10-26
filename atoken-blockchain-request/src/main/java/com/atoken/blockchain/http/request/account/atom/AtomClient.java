package com.atoken.blockchain.http.request.account.atom;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atoken.blockchain.exception.ResponseException;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.RequestClient;
import com.atoken.blockchain.http.request.RpcParams;
import com.atoken.blockchain.http.request.enums.ContentType;
import com.atoken.blockchain.http.request.enums.Method;
import net.minidev.json.JSONUtil;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AtomClient extends RequestClient {
    private String rpcPort;
    private String restPort;
    private String browserUrl;

    public AtomClient(String baseUrl, String rpcPort, String restPort, String browserUrl) {
        super(baseUrl);
        this.rpcPort = rpcPort;
        this.restPort = restPort;
        this.browserUrl = browserUrl;
    }

    public AtomNodeStatus getAtomNodeStatus() {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("status");
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(AtomNodeStatus.AtomNodeStatusResponse.class)
                .getResult();
    }

    public Integer getCurrentBlockHeight() {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("status");
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(AtomNodeStatus.AtomNodeStatusResponse.class)
                .getResult().getSyncInfo().getLatestBlockHeight();
    }

    public AtomBlockInfo getBlockByHeight(Integer blockHeight) {
        Request request = new Request();
        RpcParams rpcParams = new RpcParams();
        rpcParams.setMethod("block");
        rpcParams.setParams(Arrays.asList(blockHeight.toString()));
        return request.url(baseUrl + ":" + rpcPort)
                .setContentType(ContentType.JSON)
                .method(Method.POST)
                .data(rpcParams)
                .send(AtomBlockInfo.AtomBlockInfoResponse.class)
                .getResult();
    }

    public String sendTx(String dataTx) {
        return new Request().url(baseUrl + ":" + restPort, "txs")
                .data(JSON.parse(dataTx))
                .method(Method.POST)
                .setContentType(ContentType.JSON_UTF8)
                .send(String.class);
    }

    public AtomTxInfo getTxByTxHash(String txHash) {
        return new Request().url(baseUrl + ":" + restPort, "txs", txHash)
                .method(Method.GET)
                .send(AtomTxInfo.class);
    }

    public List<AtomAmount> getBalanceByAddress(String address) {
        List<AtomAmount> result = new Request().url(baseUrl + ":" + restPort, "bank", "balances", address)
                .method(Method.GET).send(AtomResponseWrapper.AtomAmountsResponse.class).getResult();

        return CollectionUtils.isEmpty(result) ? Arrays.asList(new AtomAmount()): result;
    }

    public AtomAccount getAtomAccountByAddress(String address) {
        return new Request().url(baseUrl + ":" + restPort, "auth", "accounts", address)
                .method(Method.GET).send(AtomResponseWrapper.AtomAccountResponse.class).getResult();
    }

    public List<AtomValidator> getAtomValidators() {
        return new Request().url(baseUrl + ":" + restPort, "staking", "validators")
                .method(Method.GET).send(AtomResponseWrapper.AtomValidatorsResponse.class).getResult();
    }

    public String getValidatorKeybaseUrl(String address) {
        try {
            return new Request().url(browserUrl, "v1", "staking", "validator", address)
                    .method(Method.GET).send(JSONObject.class).getString("keybase_url");
        } catch (Exception e) {
            return "";
        }
    }

    public List<AtomAmount> getDelegatorRewardsByValidator(String delegatorAddress, String validatorAddress) {
        try {
            return new Request().url(baseUrl + ":" + restPort, "distribution", "delegators", delegatorAddress, "rewards", validatorAddress)
                    .method(Method.GET).send(AtomResponseWrapper.AtomAmountsResponse.class).getResult();
        } catch (ResponseException e) {
            return null;
        }
    }

    public List<AtomAmount> getDelegatorRewards(String address) {
        return new Request().url(baseUrl + ":" + restPort, "distribution", "delegators", address, "rewards")
                .method(Method.GET).send(AtomResponseWrapper.AtomRewardAmountResponse.class).getResult().getTotal();
    }

    public List<AtomUnbondingDelegation> getUnbondingDelegations(String address) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", address, "unbonding_delegations")
                .method(Method.GET).send(AtomResponseWrapper.AtomUnbondingDelegationsResponse.class).getResult();
    }

    public AtomUnbondingDelegation getUnbondingDelegationsByValidator(String delegatorAddress, String validatorAddress) {
        try {
            return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", delegatorAddress, "unbonding_delegations", validatorAddress)
                    .method(Method.GET).send(AtomResponseWrapper.AtomUnbondingDelegationResponse.class).getResult();
        } catch (ResponseException e) {
            try {
                String responseStr = EntityUtils.toString(e.response().getEntity(), "utf-8");
                AtomErrorInfo atomErrorInfo = JSON.parseObject(responseStr, AtomErrorInfo.class);
//                AtomError atomError = JSON.parseObject(atomErrorInfo.getError(), AtomError.class);
//                if("no unbonding delegation found".equals(atomError.getMessage())
//                        ||"no delegation for (address, validator) tuple".equals("atomError.getError()")) {
//                    return null;
//                }
                if (JSONObject.isValidObject(atomErrorInfo.getError())) {
                    AtomError atomError = JSON.parseObject(atomErrorInfo.getError(), AtomError.class);
                    if("no unbonding delegation found".equals(atomError.getMessage())) {
                        return null;
                    }
                }
                if("no unbonding delegation found".equals(atomErrorInfo.getError())) {
                    return null;
                }

            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            throw e;
        }
    }

    public List<AtomDelegationResult> getDelegations(String address) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", address, "delegations")
                .method(Method.GET).send(AtomResponseWrapper.AtomDelegationsResponse.class).getResult();
    }

    public AtomDelegation getDelegationByValidator(String delegatorAddress, String validatorAddress) {
        try {
            return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", delegatorAddress, "delegations", validatorAddress)
                    .method(Method.GET).send(AtomResponseWrapper.AtomDelegationResponse.class).getResult();
        } catch (ResponseException e) {
            try {
                String responseStr = EntityUtils.toString(e.response().getEntity(), "utf-8");
                AtomErrorInfo atomErrorInfo = JSON.parseObject(responseStr, AtomErrorInfo.class);
                if (JSONObject.isValidObject(atomErrorInfo.getError())) {
                    AtomError atomError = JSON.parseObject(atomErrorInfo.getError(), AtomError.class);
                    if("no delegation for this (address, validator) pair".equals(atomError.getMessage())) {
                        return null;
                    }
                }
                if("no delegation for (address, validator) tuple".equals(atomErrorInfo.getError())) {
                    return null;
                }
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            throw e;
        }
    }

    public AtomStakingPool getAtomStakingPool() {
        return new Request().url(baseUrl + ":" + restPort, "staking", "pool")
                .method(Method.GET).send(AtomResponseWrapper.AtomStakingPoolResponse.class).getResult();
    }

    public List<AtomDelegator> getAtomDelegatorsByValidator(String address) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "validators", address, "delegations")
                .method(Method.GET).send(AtomResponseWrapper.AtomDelegatorsResponse.class).getResult();
    }

    public List<AtomDelegator> getAtomDelegatorsByDelegator(String address) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", address, "delegations")
                .method(Method.GET).send(AtomResponseWrapper.AtomDelegatorsResponse.class).getResult();
    }

    public AtomValidator getValidatorByAddress(String address) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "validators", address)
                .method(Method.GET).send(AtomResponseWrapper.AtomValidatorResponse.class).getResult();
    }

    public AtomMintingInfo getMintingInfo() {
        return new Request().url(baseUrl + ":" + restPort, "minting", "parameters")
                .method(Method.GET).send(AtomResponseWrapper.AtomMintingInfoResponse.class).getResult();
    }

    public AtomStakingInfo getStakingInfo() {
        return new Request().url(baseUrl + ":" + restPort, "staking", "parameters")
                .method(Method.GET).send(AtomResponseWrapper.AtomStakingInfoResponse.class).getResult();
    }

    public Double getInflation() {
        return new Request().url(baseUrl + ":" + restPort, "minting", "inflation")
                .method(Method.GET).send(AtomResponseWrapper.DoubleResponse.class).getResult();
    }

    public AtomMemPoolInfo getAtomMemPoolInfo() {
        return new Request().url(baseUrl + ":" + rpcPort, "num_unconfirmed_txs")
                .method(Method.GET).send(AtomMemPoolInfo.AtomMemPoolInfoResponse.class).getResult();
    }

    public Integer estimateMsgSendGas(AtomEstimateInfo atomEstimateInfo) {
        return new Request().url(baseUrl + ":" + restPort, "bank", "accounts", atomEstimateInfo.getBaseReq().getFrom(), "transfers")
                .method(Method.POST).setContentType(ContentType.JSON_UTF8)
                .data(atomEstimateInfo).send(AtomTx.class).getValue().getFee().getGas();
    }

    public Integer estimateMsgDelegateGas(AtomEstimateInfo atomEstimateInfo) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", atomEstimateInfo.getBaseReq().getFrom(), "delegations")
                .method(Method.POST).setContentType(ContentType.JSON_UTF8)
                .data(atomEstimateInfo).send(AtomTx.class).getValue().getFee().getGas();
    }

    public Integer estimateMsgUndelegateGas(AtomEstimateInfo atomEstimateInfo) {
        return new Request().url(baseUrl + ":" + restPort, "staking", "delegators", atomEstimateInfo.getBaseReq().getFrom(), "unbonding_delegations")
                .method(Method.POST).setContentType(ContentType.JSON_UTF8)
                .data(atomEstimateInfo).send(AtomTx.class).getValue().getFee().getGas();
    }

    public Integer estimateMsgWithdrawDelegationRewardGas(AtomEstimateInfo atomEstimateInfo) {
        return new Request().url(baseUrl + ":" + restPort, "distribution", "delegators", atomEstimateInfo.getBaseReq().getFrom(), "rewards")
                .method(Method.POST).setContentType(ContentType.JSON_UTF8)
                .data(atomEstimateInfo).send(AtomTx.class).getValue().getFee().getGas();
    }

}
