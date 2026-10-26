package com.atoken.blockchain.http.request.account.dot;


/**
 * @author susky
 * @version V1.0
 * @Title: DotHeader
 * @Package com.atoken.blockchain.http.request.account.dot
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class DotHeader {
    private Integer number;

    private String extrinsicsRoot;

    private DotDigest digest;

    private String stateRoot;

    private String parentHash;


    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public String getParentHash() {
        return this.parentHash;
    }

    public void setStateRoot(String stateRoot) {
        this.stateRoot = stateRoot;
    }

    public String getStateRoot() {
        return this.stateRoot;
    }

    public void setDigest(DotDigest digest) {
        this.digest = digest;
    }

    public DotDigest getDigest() {
        return this.digest;
    }

    public void setExtrinsicsRoot(String extrinsicsRoot) {
        this.extrinsicsRoot = extrinsicsRoot;
    }

    public String getExtrinsicsRoot() {
        return this.extrinsicsRoot;
    }

    public void setNumber(String number) {
        this.number = Integer.valueOf(number.substring(2), 16);
    }

    public Integer getNumber() {
        return this.number;
    }


}
