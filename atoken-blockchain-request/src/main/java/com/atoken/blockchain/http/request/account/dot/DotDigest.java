package com.atoken.blockchain.http.request.account.dot;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: DotDigest
 * @Package com.atoken.blockchain.http.request.account.dot
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class DotDigest {
    private List<String> logs;


    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public List<String> getLogs() {
        return this.logs;
    }

}
