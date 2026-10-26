package com.atoken.blockchain.utils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by qingyun.yu on 2018/11/24.
 */
public class ResponseUtil {
    public static String parseResponse(HttpResponse response) {
        try {
            return EntityUtils.toString(response.getEntity(), "utf-8");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
