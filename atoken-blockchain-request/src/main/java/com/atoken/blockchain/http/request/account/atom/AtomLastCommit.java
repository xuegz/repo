package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomLastCommit
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomLastCommit {
    private AtomBlockId blockId;

    private List<AtomPrecommit> precommits;


    public void setPrecommits(List<AtomPrecommit> precommits) {
        this.precommits = precommits;
    }

    public List<AtomPrecommit> getPrecommits() {
        return this.precommits;
    }

    public void setBlockId(AtomBlockId blockId) {
        this.blockId = blockId;
    }

    public AtomBlockId getBlockId() {
        return this.blockId;
    }

}
