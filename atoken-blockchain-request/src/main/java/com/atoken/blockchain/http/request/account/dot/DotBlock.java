package com.atoken.blockchain.http.request.account.dot;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: DotBlock
 * @Package com.atoken.blockchain.http.request.account.dot
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class DotBlock {
    private List<String> extrinsics;

    private DotHeader header;


    public void setHeader(DotHeader header) {
        this.header = header;
    }

    public DotHeader getHeader() {
        return this.header;
    }

    public void setExtrinsics(List<String> extrinsics) {
        this.extrinsics = extrinsics;
    }

    public List<String> getExtrinsics() {
        return this.extrinsics;
    }

}
