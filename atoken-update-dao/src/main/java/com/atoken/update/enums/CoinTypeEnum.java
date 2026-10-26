package com.atoken.update.enums;


/**
 * @author legends_bai
 * @date 2020/12/17 下午5:40
 */

public enum  CoinTypeEnum {
    xwc(0,"xwc"),
    ht(1,"ht"),
    bnb(2, "bnb"),
    kar(3, "kar"),
    ksm(4,"ksm"),
    dot(4,"dot");

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    private Integer code;
    private String coinName;
    //无质押币种枚举


    CoinTypeEnum(Integer code, String coinName) {
        this.code = code;
        this.coinName = coinName;
    }

    public static boolean contains(String coinType){
        for(CoinTypeEnum coinTypeEnum : CoinTypeEnum.values()){
            if (coinTypeEnum.coinName.equals(coinType)) {
                return true;
            }
        }
        return false;
    }

}
