package com.atoken.blockchain.http.request.account.ont;

import java.util.List;

public class OntContractList {
    private List<OntContractInfo> contractList;
    private Integer total;

    public List<OntContractInfo> getContractList() {
        return contractList;
    }

    public void setContractList(List<OntContractInfo> contractList) {
        this.contractList = contractList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
