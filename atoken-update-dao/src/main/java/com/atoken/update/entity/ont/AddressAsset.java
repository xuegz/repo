package com.atoken.update.entity.ont;

import java.math.BigDecimal;
import java.util.Date;

public class AddressAsset {

    private Long id;

    private String contractAddr;

    private String assetCode;

    private String addr;

    private String added;

    private BigDecimal amount;

    private String walletId;

    private String assetType;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractAddr() {
        return contractAddr;
    }

    public void setContractAddr(String contractAddr) {
        this.contractAddr = contractAddr == null ? null : contractAddr.trim();
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode == null ? null : assetCode.trim();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added == null ? null : added.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId == null ? null : walletId.trim();
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

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public static AddressAsset txToAddressAsset(Tx tx, boolean isFrom) {
        AddressAsset addressAsset = new AddressAsset();
        addressAsset.setContractAddr(tx.getContractAddr());
        if(isFrom) {
            addressAsset.setAddr(tx.getFromAddr());
            addressAsset.setAmount(tx.getAmount().negate());
        } else {
            addressAsset.setAddr(tx.getToAddr());
            addressAsset.setAmount(tx.getAmount());
        }
        return addressAsset;
    }

    public static AddressAsset txToAddressAssetForRollBack(Tx tx, boolean isFrom) {
        AddressAsset addressAsset = new AddressAsset();
        addressAsset.setContractAddr(tx.getContractAddr());
        if(isFrom) {
            addressAsset.setAddr(tx.getFromAddr());
            addressAsset.setAmount(tx.getAmount());
        } else {
            addressAsset.setAddr(tx.getToAddr());
            addressAsset.setAmount(tx.getAmount().negate());
        }
        return addressAsset;
    }
}