package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: erwin
 * @time: 2020/9/9 3:23 下午
 */
@Data
public class InfoDTO {

    private BigDecimal weight;

    @JSONField(name = "class")
    private String classType;

    private BigDecimal partialFee;

}
