package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomBlock
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomBlock {
    private List<String> evidence;

    private AtomLastCommit lastCommit;

    private AtomHeader header;

    private AtomData data;

    public AtomData getData() {
        return data;
    }

    public void setData(AtomData data) {
        this.data = data;
    }

    public void setHeader(AtomHeader header) {
        this.header = header;
    }

    public AtomHeader getHeader() {
        return this.header;
    }

    public void setLastCommit(AtomLastCommit lastCommit) {
        this.lastCommit = lastCommit;
    }

    public AtomLastCommit getLastCommit() {
        return this.lastCommit;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public List<String> getEvidence() {
        return this.evidence;
    }

}
