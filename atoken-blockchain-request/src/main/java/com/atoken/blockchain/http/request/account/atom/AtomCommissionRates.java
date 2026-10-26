package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.utils.DateUtil;

import java.util.Date;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomCommission
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomCommissionRates {
    private Double rate;

    private Double maxRate;

    private Double maxChangeRate;

    public void setMaxChangeRate(Double maxChangeRate) {
        this.maxChangeRate = maxChangeRate;
    }

    public Double getMaxChangeRate() {
        return this.maxChangeRate;
    }

    public void setMaxRate(Double maxRate) {
        this.maxRate = maxRate;
    }

    public Double getMaxRate() {
        return this.maxRate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return this.rate;
    }

}
