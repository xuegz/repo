package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2020/9/9 3:11 下午
 */
@Data
public class LogsDTO {

    private String type;
    private String index;
    private List<String> value;
}
