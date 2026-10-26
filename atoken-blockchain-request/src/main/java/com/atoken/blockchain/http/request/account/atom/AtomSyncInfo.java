package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomSyncInfo
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomSyncInfo {
    private String latestBlockTime;

    private String latestBlockHash;

    private String latestAppHash;

    private Boolean catchingUp;

    private Integer latestBlockHeight;

    private String earliestBlockTime;

    private String earliestBlockHash;

    private String earliestAppHash;

    private Integer earliestBlockHeight;

    public String getEarliestBlockTime() {
        return earliestBlockTime;
    }

    public void setEarliestBlockTime(String earliestBlockTime) {
        this.earliestBlockTime = earliestBlockTime;
    }

    public String getEarliestBlockHash() {
        return earliestBlockHash;
    }

    public void setEarliestBlockHash(String earliestBlockHash) {
        this.earliestBlockHash = earliestBlockHash;
    }

    public String getEarliestAppHash() {
        return earliestAppHash;
    }

    public void setEarliestAppHash(String earliestAppHash) {
        this.earliestAppHash = earliestAppHash;
    }

    public Integer getEarliestBlockHeight() {
        return earliestBlockHeight;
    }

    public void setEarliestBlockHeight(Integer earliestBlockHeight) {
        this.earliestBlockHeight = earliestBlockHeight;
    }

    public void setLatestBlockHeight(Integer latestBlockHeight) {
        this.latestBlockHeight = latestBlockHeight;
    }

    public Integer getLatestBlockHeight() {
        return this.latestBlockHeight;
    }

    public void setCatchingUp(Boolean catchingUp) {
        this.catchingUp = catchingUp;
    }

    public Boolean getCatchingUp() {
        return this.catchingUp;
    }

    public void setLatestAppHash(String latestAppHash) {
        this.latestAppHash = latestAppHash;
    }

    public String getLatestAppHash() {
        return this.latestAppHash;
    }

    public void setLatestBlockHash(String latestBlockHash) {
        this.latestBlockHash = latestBlockHash;
    }

    public String getLatestBlockHash() {
        return this.latestBlockHash;
    }

    public void setLatestBlockTime(String latestBlockTime) {
        this.latestBlockTime = latestBlockTime;
    }

    public String getLatestBlockTime() {
        return this.latestBlockTime;
    }

}
