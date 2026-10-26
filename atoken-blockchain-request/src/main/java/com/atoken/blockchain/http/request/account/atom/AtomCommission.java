package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.utils.DateUtil;

import java.util.Date;
import java.util.Objects;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomCommission
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomCommission {
    private Date updateTime;

    private AtomCommissionRates commissionRates;

    private Double rate;

    private Double maxRate;

    private Double maxChangeRate;

    public AtomCommissionRates getCommissionRates() {
        return commissionRates;
    }

    public void setCommissionRates(AtomCommissionRates commissionRates) {
        this.commissionRates = commissionRates;
        this.rate = commissionRates.getRate();
        this.maxRate = commissionRates.getMaxRate();
        this.maxChangeRate = commissionRates.getMaxChangeRate();
    }

    public void setMaxChangeRate(Double maxChangeRate) {
        if(Objects.nonNull(maxChangeRate)) {
            this.maxChangeRate = maxChangeRate;
        }
    }

    public Double getMaxChangeRate() {
        return this.maxChangeRate;
    }

    public void setMaxRate(Double maxRate) {
        if(Objects.nonNull(maxRate)) {
            this.maxRate = maxRate;
        }
    }

    public Double getMaxRate() {
        return this.maxRate;
    }

    public void setRate(Double rate) {
        if(Objects.nonNull(rate)) {
            this.rate = rate;
        }
    }

    public Double getRate() {
        return this.rate;
    }

    public void setUpdateTime(String updateTime) {
        Integer index = updateTime.indexOf('.');
        this.updateTime = DateUtil.strToDate(updateTime.substring(0,  index == -1? updateTime.length() - 1: index) + "Z", DateUtil.FORMAT_ALL);
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }
}
