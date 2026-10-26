package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2020/9/9 3:19 下午
 */
@Data
public class EventDTO {

    private MethodDTO method;

    private List<String> data;
}
