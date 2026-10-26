package com.atoken.update.entity;

import java.util.Date;

public class ImportAddressBean {
    /**
     * ID
     */
    private Integer id;

    /**
     * 钱包id
     */
    private String walletId;

    /**
     * 币种简称（大写，例如：BTC）
     */
    private String coinType;

    /**
     * address or account_name
     */
    private String addr;

    /**
     * 处理结果（0:未处理, 1:已处理, 2:处理失败）
     */
    private Integer status;

    /**
     * 记录状态（0:有效 1:无效）
     */
    private Integer del;

    /**
     * 处理次数，超过三次失败，变成2:处理失败
     */
    private Integer count;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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