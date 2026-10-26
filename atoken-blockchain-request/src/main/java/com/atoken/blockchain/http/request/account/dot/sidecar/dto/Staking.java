package com.atoken.blockchain.http.request.account.dot.sidecar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Auto-generated: 2021-06-08 16:34:40
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class Staking {

    private String stash;
    private String total;
    private String active;
    private List<Unlocking> unlocking;
    @JsonProperty("claimedRewards")
    private List<String> claimedrewards;
    public void setStash(String stash) {
         this.stash = stash;
     }
     public String getStash() {
         return stash;
     }

    public void setTotal(String total) {
         this.total = total;
     }
     public String getTotal() {
         return total;
     }

    public void setActive(String active) {
         this.active = active;
     }
     public String getActive() {
         return active;
     }

    public void setUnlocking(List<Unlocking> unlocking) {
         this.unlocking = unlocking;
     }
     public List<Unlocking> getUnlocking() {
         return unlocking;
     }

    public void setClaimedrewards(List<String> claimedrewards) {
         this.claimedrewards = claimedrewards;
     }
     public List<String> getClaimedrewards() {
         return claimedrewards;
     }

}