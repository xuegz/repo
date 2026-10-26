package com.atoken.blockchain.utils;

import java.math.BigDecimal;

/**
 * Created by qingyun.yu on 2018/11/30.
 */
public class SCUtil {
    public static BigDecimal conventAmount(BigDecimal price) {
        BigDecimal coefficient = new BigDecimal("1000000000000000000000000");
        return price.divide(coefficient).setScale(4, BigDecimal.ROUND_DOWN);
    }
}
