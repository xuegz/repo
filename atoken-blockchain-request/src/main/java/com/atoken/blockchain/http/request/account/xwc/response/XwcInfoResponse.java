package com.atoken.blockchain.http.request.account.xwc.response;

import com.atoken.blockchain.http.request.RpcResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

/**
 * @author legends_bai
 * @date 2020/12/8 下午5:18
 */
public class XwcInfoResponse {

    @JsonProperty("head_block_num")
    private Integer headBlockNum;
    @JsonProperty("head_block_id")
    private String headBlockId;
    @JsonProperty("head_block_age")
    private String headBlockAge;
    private String version;

    public Integer getHeadBlockNum() {
        return headBlockNum;
    }

    public void setHeadBlockNum(Integer headBlockNum) {
        this.headBlockNum = headBlockNum;
    }

    public String getHeadBlockId() {
        return headBlockId;
    }

    public void setHeadBlockId(String headBlockId) {
        this.headBlockId = headBlockId;
    }

    public String getHeadBlockAge() {
        return headBlockAge;
    }

    public void setHeadBlockAge(String headBlockAge) {
        this.headBlockAge = headBlockAge;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNextMaintenanceTime() {
        return nextMaintenanceTime;
    }

    public void setNextMaintenanceTime(String nextMaintenanceTime) {
        this.nextMaintenanceTime = nextMaintenanceTime;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getParaticipation() {
        return paraticipation;
    }

    public void setParaticipation(String paraticipation) {
        this.paraticipation = paraticipation;
    }

    public String getRoundParticipation() {
        return roundParticipation;
    }

    public void setRoundParticipation(String roundParticipation) {
        this.roundParticipation = roundParticipation;
    }

    public List<String> getScheduledCitizens() {
        return scheduledCitizens;
    }

    public void setScheduledCitizens(List<String> scheduledCitizens) {
        this.scheduledCitizens = scheduledCitizens;
    }

    @JsonProperty("next_maintenance_time")
    private String nextMaintenanceTime;
    @JsonProperty("chain_id")
    private String chainId;
    private String paraticipation;
    @JsonProperty("round_participation")
    private String roundParticipation;
    @JsonProperty("scheduled_citizens")
    private List<String> scheduledCitizens;

    public static class XwcInfoRpcResponse extends RpcResponse<XwcInfoResponse> {
    }
}
