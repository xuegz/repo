package com.atoken.update.dingding.annotaion.handler;


import java.util.Map;

public interface AlarmBaseHandler {
    /**
     * 哪些是需要发送的
     * @param paramInfoMap
     * @return true需要发送 false不需要发送
     */
    boolean handler(Map<String, ParamInfo> paramInfoMap);
}
