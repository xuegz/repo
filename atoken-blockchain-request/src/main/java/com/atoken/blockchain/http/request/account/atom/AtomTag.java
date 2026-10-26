package com.atoken.blockchain.http.request.account.atom;


/**
 * @author susky
 * @version V1.0
 * @Title: AtomTag
 * @Package com.atoken.blockchain.http.request.account.atom
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class AtomTag {
    private String value;

    private String key;


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
