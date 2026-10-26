package com.atoken.blockchain.http.request.account.tomo;

import java.math.BigDecimal;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoMasterNode
 * @Package com.ninedish.domain.vo
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoMasterNode {
    private String owner;

    private Boolean isMasternode;

    private String capacity;

    private String createdAt;

    private String candidate;

    private Integer v;

    private String name;

    private String smartContractAddress;

    private BigDecimal capacityNumber;

    private Integer rank;

    private String id;

    private TomoSocials socials;

    private String nodeId;

    private Long latestSignedBlock;

    private String status;

    private String updatedAt;


    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setLatestSignedBlock(Long latestSignedBlock) {
        this.latestSignedBlock = latestSignedBlock;
    }

    public Long getLatestSignedBlock() {
        return this.latestSignedBlock;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setSocials(TomoSocials socials) {
        this.socials = socials;
    }

    public TomoSocials getSocials() {
        return this.socials;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getRank() {
        return this.rank;
    }

    public void setCapacityNumber(BigDecimal capacityNumber) {
        this.capacityNumber = capacityNumber;
    }

    public BigDecimal getCapacityNumber() {
        return this.capacityNumber;
    }

    public void setSmartContractAddress(String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }

    public String getSmartContractAddress() {
        return this.smartContractAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public Integer getV() {
        return this.v;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getCandidate() {
        return this.candidate;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCapacity() {
        return this.capacity;
    }

    public void setIsMasternode(Boolean isMasternode) {
        this.isMasternode = isMasternode;
    }

    public Boolean getIsMasternode() {
        return this.isMasternode;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

}
