package com.atoken.blockchain.http.request.account.atom;

public class AtomMintingInfo {
    private String mintDenom;
    private Double inflationRateChange;
    private Double inflationMax;
    private Double inflationMin;
    private Double goalBonded;
    private Integer blocksPerYear;

    public String getMintDenom() {
        return mintDenom;
    }

    public void setMintDenom(String mintDenom) {
        this.mintDenom = mintDenom;
    }

    public Double getInflationRateChange() {
        return inflationRateChange;
    }

    public void setInflationRateChange(Double inflationRateChange) {
        this.inflationRateChange = inflationRateChange;
    }

    public Double getInflationMax() {
        return inflationMax;
    }

    public void setInflationMax(Double inflationMax) {
        this.inflationMax = inflationMax;
    }

    public Double getInflationMin() {
        return inflationMin;
    }

    public void setInflationMin(Double inflationMin) {
        this.inflationMin = inflationMin;
    }

    public Double getGoalBonded() {
        return goalBonded;
    }

    public void setGoalBonded(Double goalBonded) {
        this.goalBonded = goalBonded;
    }

    public Integer getBlocksPerYear() {
        return blocksPerYear;
    }

    public void setBlocksPerYear(Integer blocksPerYear) {
        this.blocksPerYear = blocksPerYear;
    }
}
