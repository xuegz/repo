package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomNodeInfo
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomNodeInfo {
    private AtomVersion protocolVersion;

    private AtomOther other;

    private String channels;

    private String listenAddr;

    private String id;

    private String moniker;

    private String version;

    private String network;


    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetwork() {
        return this.network;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setMoniker(String moniker) {
        this.moniker = moniker;
    }

    public String getMoniker() {
        return this.moniker;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setListenAddr(String listenAddr) {
        this.listenAddr = listenAddr;
    }

    public String getListenAddr() {
        return this.listenAddr;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getChannels() {
        return this.channels;
    }

    public void setOther(AtomOther other) {
        this.other = other;
    }

    public AtomOther getOther() {
        return this.other;
    }

    public void setProtocolVersion(AtomVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public AtomVersion getProtocolVersion() {
        return this.protocolVersion;
    }

}
