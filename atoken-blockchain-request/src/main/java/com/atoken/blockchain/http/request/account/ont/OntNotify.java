package com.atoken.blockchain.http.request.account.ont;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OntNotify {
    private String contractAddress;
    private OntTransfer transfer;
    private List<Object> states;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public List<Object> getStates() {
        return states;
    }

    public void setStates(List<Object> states) {
        this.states = states;
        if ("transfer".equals(states.get(0)) || "transferFrom".equals(states.get(0))) {
            OntTransfer transfer = new OntTransfer();
            transfer.setMethod(OntMethod.initOntMethod(states.get(0).toString()));
            transfer.setFrom(states.get(1).toString());
            transfer.setTo(states.get(2).toString());
            transfer.setAmount(new BigDecimal(states.get(3).toString()));
            this.transfer = transfer;
        }
    }

    public OntTransfer getTransfer() {
        return transfer;
    }
}
