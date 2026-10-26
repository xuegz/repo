package com.atoken.blockchain.http.request.account.tomo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoVotedCandidates
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoVotedCandidates {
    private Integer total;

    private BigDecimal totalVoted;

    private List<TomoVotedCandidate> items;


    public void setItems(List<TomoVotedCandidate> items) {
        this.items = items;
    }

    public List<TomoVotedCandidate> getItems() {
        return this.items;
    }

    public void setTotalVoted(BigDecimal totalVoted) {
        this.totalVoted = totalVoted;
    }

    public BigDecimal getTotalVoted() {
        return this.totalVoted;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return this.total;
    }

}
