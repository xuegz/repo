package com.atoken.blockchain.http.request.account.tomo;


/**
 * @author susky
 * @version V1.0
 * @Title: TomoSocials
 * @Package com.ninedish.domain.vo
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoSocials {
    private String website;

    private String telegram;


    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public String getTelegram() {
        return this.telegram;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return this.website;
    }

}
