package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomBlockMeta
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomBlockMeta {
    private AtomHeader header;

    private AtomBlockId blockId;


    public void setBlockId(AtomBlockId blockId) {
        this.blockId = blockId;
    }

    public AtomBlockId getBlockId() {
        return this.blockId;
    }

    public void setHeader(AtomHeader header) {
        this.header = header;
    }

    public AtomHeader getHeader() {
        return this.header;
    }

}
