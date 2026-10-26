package com.atoken.blockchain.http.request.account.ont;

import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: OntPreTransaction
 * @Package com.atoken.blockchain.http.request.account.ont
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class OntPreTransaction {
    private OntNotify ontNotify;

    private Integer state;

    private BigDecimal gas;

    private String result;


    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }

    public void setGas(BigDecimal gas) {
        this.gas = gas;
    }

    public BigDecimal getGas() {
        return this.gas;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return this.state;
    }

    public void setOntNotify(OntNotify ontNotify) {
        this.ontNotify = ontNotify;
    }

    public OntNotify getOntNotify() {
        return this.ontNotify;
    }

}
