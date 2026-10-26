package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author: erwin
 * @time: 2020/9/9 3:02 下午
 */
@Data
public class ExtrinsicsDTO {

    private MethodDTO method;

    private SignatureDTO signature;

    private Integer nonce;
    private Map<String, String> args;
    private BigDecimal tip;
    private String hash;
    private InfoDTO info;

    private List<EventDTO> events;
    private Boolean success;
    private Boolean paysFee;

}