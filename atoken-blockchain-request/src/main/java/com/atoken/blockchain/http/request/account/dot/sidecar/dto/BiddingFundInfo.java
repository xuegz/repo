package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import com.atoken.blockchain.http.request.account.dot.sidecar.dto.At;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.Rewarddestination;
import com.atoken.blockchain.http.request.account.dot.sidecar.dto.Staking;
import lombok.Data;

import java.util.List;

/**
 * @author: erwin
 * @time: 2021/4/1 3:51 下午
 */
@Data
public class BiddingFundInfo {
    private String raised;
    private String cap;
    private String firstPeriod;
    private String lastPeriod;
    private String trieIndex;
}
