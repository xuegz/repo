package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2020/9/9 3:02 下午
 */
@Data
public class OnInitializeDTO {

    private List<String> events;
}
