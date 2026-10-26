package com.atoken.blockchain.http.request.account.dot;


/**
 * @author susky
 * @version V1.0
 * @Title: DotBlockInfo
 * @Package com.atoken.blockchain.http.request.account.dot
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class DotBlockInfo {
    private DotBlock block;

    private String justification;


    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getJustification() {
        return this.justification;
    }

    public void setBlock(DotBlock block) {
        this.block = block;
    }

    public DotBlock getBlock() {
        return this.block;
    }

}
