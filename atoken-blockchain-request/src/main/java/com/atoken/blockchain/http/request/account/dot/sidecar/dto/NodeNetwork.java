package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/1 2:41 下午
 */
@Data
public class NodeNetwork {

    private List<NodeRolesItem> nodeRoles;
    private String numPeers;
    private boolean isSyncing;

    private boolean shouldHavePeers;
    private String localPeerId;
    private List<String> localListenAddresses;
    private String peersInfo;

}