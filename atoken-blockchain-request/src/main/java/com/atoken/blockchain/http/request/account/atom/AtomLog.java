package com.atoken.blockchain.http.request.account.atom;


import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomLog
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomLog {
    private String log;

    private Boolean success;

    private Integer msgIndex;

    private List<AtomEvent> events;


    public void setMsgIndex(Integer msgIndex) {
        this.msgIndex = msgIndex;
    }

    public Integer getMsgIndex() {
        return this.msgIndex;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return this.log;
    }

    public List<AtomEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AtomEvent> events) {
        this.events = events;
    }
}
