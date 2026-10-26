package com.atoken.blockchain.http.request.account.tomo;

import java.util.List;

/**
 * @author susky
 * @version V1.0
 * @Title: TomoVoterRewards
 * @Description:
 * @date 2019/2/13 3:45 PM
 */
public class TomoVoterRewards {
    private Integer total;

    private Integer perPage;

    private Integer pages;

    private Integer currentPage;

    private List<TomoVoterReward> items;


    public void setItems(List<TomoVoterReward> items) {
        this.items = items;
    }

    public List<TomoVoterReward> getItems() {
        return this.items;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPages() {
        return this.pages;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getPerPage() {
        return this.perPage;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return this.total;
    }

}
