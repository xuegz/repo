package com.atoken.update.entity.dot;

import java.util.Date;

public class DotNominateExtrinsic {
    private Long id;

    private String bondHash;

    private String hash;

    private String extrinsic;

    private String status;

    private String errorMessage;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBondHash() {
        return bondHash;
    }

    public void setBondHash(String bondHash) {
        this.bondHash = bondHash == null ? null : bondHash.trim();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public String getExtrinsic() {
        return extrinsic;
    }

    public void setExtrinsic(String extrinsic) {
        this.extrinsic = extrinsic == null ? null : extrinsic.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? null : errorMessage.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}