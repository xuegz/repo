package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/1 3:51 下午
 */
@Data
public class BiddingInfoDTO {
    private At at;
    private List<BiddingFundInfoDTO> funds;
}
