package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/7 10:50 上午
 */
@Data
public class Exposure {
    private String total;
    private String own;
    private List<OthersItem> others;
}