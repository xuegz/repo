package com.atoken.blockchain.http.request.account.atom;

import com.atoken.blockchain.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomTxInfo
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomTxInfo {
    private AtomTx tx;

    private Integer gasUsed;

    private Integer gasWanted;

    private String rawLog;

    private List<AtomLog> logs;

    private Integer height;

    private String txhash;

    private List<AtomTag> tags;

    private Date timestamp;


    public void setTimestamp(String timestamp) {
        Integer index = timestamp.indexOf('.');
        this.timestamp = DateUtil.strToDate(timestamp.substring(0, index == -1? timestamp.length() - 1: index) + "Z", DateUtil.FORMAT_ALL);
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTags(List<AtomTag> tags) {
        this.tags = tags;
    }

    public List<AtomTag> getTags() {
        return this.tags;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    public String getTxhash() {
        return this.txhash;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setLogs(List<AtomLog> logs) {
        this.logs = logs;
    }

    public List<AtomLog> getLogs() {
        return this.logs;
    }

    public void setRawLog(String rawLog) {
        this.rawLog = rawLog;
    }

    public String getRawLog() {
        return this.rawLog;
    }

    public void setGasWanted(Integer gasWanted) {
        this.gasWanted = gasWanted;
    }

    public Integer getGasWanted() {
        return this.gasWanted;
    }

    public void setGasUsed(Integer gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Integer getGasUsed() {
        return this.gasUsed;
    }

    public void setTx(AtomTx tx) {
        this.tx = tx;
    }

    public AtomTx getTx() {
        return this.tx;
    }
}
