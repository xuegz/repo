package com.atoken.blockchain.http.request.account.atom;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomBaseReq
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomBaseReq {

    @JSONField(name = "account_number")
    private String accountNumber = "1";

    private String sequence = "0";

    @JSONField(name = "chain_id")
    private String chainId = "Cosmos-Hub_2";

    @JSONField(name = "gas_adjustment")
    private String gasAdjustment = "1";

    private String gas = "auto";

    private String memo = "";

    private String from;

    private Boolean simulate = false;

    private List<AtomAmount> fees;

    public List<AtomAmount> getFees() {
        return fees;
    }

    public void setFees(List<AtomAmount> fees) {
        this.fees = fees;
    }

    public void setSimulate(Boolean simulate) {
        this.simulate = simulate;
    }

    public Boolean getSimulate() {
        return this.simulate;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGas() {
        return this.gas;
    }

    public void setGasAdjustment(String gasAdjustment) {
        this.gasAdjustment = gasAdjustment;
    }

    public String getGasAdjustment() {
        return this.gasAdjustment;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getChainId() {
        return this.chainId;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return this.sequence;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

}
