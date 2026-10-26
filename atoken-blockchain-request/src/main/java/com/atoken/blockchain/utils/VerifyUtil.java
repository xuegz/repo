package com.atoken.blockchain.utils;

import java.util.Objects;

/**
 * Created by qingyun.yu on 2018/11/23.
 */
public class VerifyUtil {
    public static void objectNotNull(Object object, String message) {
        if(Objects.isNull(object)) {
            throw new RuntimeException(message);
        }
    }

    public static void numberNotZore(Integer rowNum, String message) {
        if (rowNum <= 0) {
            throw new RuntimeException(message);
        }
    }

    public static void objectEquals(Object a, Object b, String message) {
        if(!a.equals(b)) {
            throw new RuntimeException(message);
        }
    }
}
