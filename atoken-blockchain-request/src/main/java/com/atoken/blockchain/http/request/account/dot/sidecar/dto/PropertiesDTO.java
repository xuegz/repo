package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

/**
 *
 * @author: erwin
 * @time: 2020/9/15 6:30 下午
 */
@Data
public class PropertiesDTO {
    private String ss58Format;
    private String tokenDecimals;
    private String tokenSymbol;
}
