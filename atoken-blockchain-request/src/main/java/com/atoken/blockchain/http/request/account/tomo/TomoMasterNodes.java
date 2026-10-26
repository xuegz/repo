package com.atoken.blockchain.http.request.account.tomo;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoMasterNodes
 * @Package com.ninedish.domain.vo
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoMasterNodes {
    private Integer total;

    private List<TomoMasterNode> items;

    private Integer activeCandidates;


    public void setActiveCandidates(Integer activeCandidates) {
        this.activeCandidates = activeCandidates;
    }

    public Integer getActiveCandidates() {
        return this.activeCandidates;
    }

    public void setItems(List<TomoMasterNode> items) {
        this.items = items;
    }

    public List<TomoMasterNode> getItems() {
        return this.items;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return this.total;
    }

}
