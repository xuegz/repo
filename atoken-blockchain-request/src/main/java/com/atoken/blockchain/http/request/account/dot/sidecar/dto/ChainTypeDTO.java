package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author: erwin
 * @time: 2020/9/15 6:31 下午
 */
@Data
public class ChainTypeDTO {

    @JSONField(name = "Live")
    private Object live;

}
