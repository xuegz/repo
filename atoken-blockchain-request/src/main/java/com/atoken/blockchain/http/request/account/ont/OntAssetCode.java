package com.atoken.blockchain.http.request.account.ont;

import java.util.stream.Stream;

public enum OntAssetCode {
    ONT("0000000000000000000000000000000000000001", "0100000000000000000000000000000000000000", "ont", 0),
    ONG("0000000000000000000000000000000000000002", "0200000000000000000000000000000000000000","ong", 9);

    private String assetCode;
    private String contractAddress;
    private String assetName;
    private Integer decimals;

    OntAssetCode(String assetCode, String contractAddress, String assetName, Integer decimals) {
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.decimals = decimals;
    }

    public String assetCode() {
        return this.assetCode;
    }

    public String assetName() {
        return this.assetName;
    }

    public Integer decimals() {
        return this.decimals;
    }

    public static OntAssetCode ontAssetCode(String assetCode) {
        return Stream.of(OntAssetCode.values()).filter(value -> value.assetCode.equals(assetCode)).findFirst().get();
    }

    public static OntAssetCode ontAssetCodeByContractAddress(String contractAddress) {
        return Stream.of(OntAssetCode.values()).filter(value -> value.contractAddress.equals(contractAddress)).findFirst().get();
    }
}
