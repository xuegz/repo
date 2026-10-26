package com.atoken.blockchain.http.request.account.tomo;

import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoVotedCandidate
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoVotedCandidate {
    private String owner;

    private String candidate;

    private String candidateName;

    private BigDecimal totalCapacity;

    private BigDecimal capacityNumber;

    private BigDecimal capacity;

    private String status;


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getCapacity() {
        return this.capacity;
    }

    public void setCapacityNumber(BigDecimal capacityNumber) {
        this.capacityNumber = capacityNumber;
    }

    public BigDecimal getCapacityNumber() {
        return this.capacityNumber;
    }

    public void setTotalCapacity(BigDecimal totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public BigDecimal getTotalCapacity() {
        return this.totalCapacity;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateName() {
        return this.candidateName;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getCandidate() {
        return this.candidate;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

}
