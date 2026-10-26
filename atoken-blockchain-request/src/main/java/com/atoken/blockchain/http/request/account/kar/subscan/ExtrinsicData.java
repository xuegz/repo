package com.atoken.blockchain.http.request.account.kar.subscan;

import java.util.List;

/**
 * @author DFG
 * @date 2021-07-28
 */
public class ExtrinsicData {

    Long block_timestamp;

    Integer block_num;

    String hash;

    String parent_hash;

    List<Extrinsics> extrinsics;

    public Long getBlock_timestamp() {
        return block_timestamp;
    }

    public void setBlock_timestamp(Long block_timestamp) {
        this.block_timestamp = block_timestamp;
    }

    public Integer getBlock_num() {
        return block_num;
    }

    public void setBlock_num(Integer block_num) {
        this.block_num = block_num;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getParent_hash() {
        return parent_hash;
    }

    public void setParent_hash(String parent_hash) {
        this.parent_hash = parent_hash;
    }

    public List<Extrinsics> getExtrinsics() {
        return extrinsics;
    }

    public void setExtrinsics(List<Extrinsics> extrinsics) {
        this.extrinsics = extrinsics;
    }
}
