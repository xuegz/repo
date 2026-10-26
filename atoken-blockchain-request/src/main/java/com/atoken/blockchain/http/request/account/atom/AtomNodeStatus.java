package com.atoken.blockchain.http.request.account.atom;


import com.atoken.blockchain.http.request.RpcResponse;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomNodeStatus
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomNodeStatus {
    private AtomNodeInfo nodeInfo;

    private AtomValidatorInfo validatorInfo;

    private AtomSyncInfo syncInfo;


    public void setSyncInfo(AtomSyncInfo syncInfo) {
        this.syncInfo = syncInfo;
    }

    public AtomSyncInfo getSyncInfo() {
        return this.syncInfo;
    }

    public void setValidatorInfo(AtomValidatorInfo validatorInfo) {
        this.validatorInfo = validatorInfo;
    }

    public AtomValidatorInfo getValidatorInfo() {
        return this.validatorInfo;
    }

    public void setNodeInfo(AtomNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public AtomNodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    public static class AtomNodeStatusResponse extends RpcResponse<AtomNodeStatus> {
    }
}
