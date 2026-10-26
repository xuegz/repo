package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.utils.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomEntries
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomEntries {
    private Integer creationHeight;

    private Date completionTime;

    private BigDecimal balance;

    private BigDecimal initialBalance;


    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getInitialBalance() {
        return this.initialBalance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setCompletionTime(String completionTime) {
        Integer index = completionTime.indexOf('.');
        this.completionTime = DateUtil.strToDate(completionTime.substring(0,  index == -1? completionTime.length() - 1: index) + "Z", DateUtil.FORMAT_ALL);
    }

    public Date getCompletionTime() {
        return this.completionTime;
    }

    public void setCreationHeight(Integer creationHeight) {
        this.creationHeight = creationHeight;
    }

    public Integer getCreationHeight() {
        return this.creationHeight;
    }

}
