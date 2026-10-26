package com.atoken.blockchain.http.request.account.kar.subscan;

/**
 * @author DFG
 * @date 2021-07-28
 */
public class Extrinsics {

    private Long block_timestamp;

    private Long block_num;

    private String extrinsic_index;

    private String account_id;

    private String fee;

    private Integer nonce;

    public Long getBlock_timestamp() {
        return block_timestamp;
    }

    public void setBlock_timestamp(Long block_timestamp) {
        this.block_timestamp = block_timestamp;
    }

    public Long getBlock_num() {
        return block_num;
    }

    public void setBlock_num(Long block_num) {
        this.block_num = block_num;
    }

    public String getExtrinsic_index() {
        return extrinsic_index;
    }

    public void setExtrinsic_index(String extrinsic_index) {
        this.extrinsic_index = extrinsic_index;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }
}
