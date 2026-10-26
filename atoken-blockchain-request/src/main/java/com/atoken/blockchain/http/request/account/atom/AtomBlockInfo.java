package com.atoken.blockchain.http.request.account.atom;


import com.atoken.blockchain.http.request.RpcResponse;

/**
 * @author susky
 * @version V1.0
 * @Title: AtomBlockInfo
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomBlockInfo {
    private AtomBlock block;

    private AtomBlockId blockId;

    public AtomBlockId getBlockId() {
        return blockId;
    }

    public void setBlockId(AtomBlockId blockId) {
        this.blockId = blockId;
    }

    public void setBlock(AtomBlock block) {
        this.block = block;
    }

    public AtomBlock getBlock() {
        return this.block;
    }

    public static class AtomBlockInfoResponse extends RpcResponse<AtomBlockInfo> {
    }

}
