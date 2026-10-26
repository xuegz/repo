package com.atoken.blockchain.http.request.account.tomo;

import java.util.Arrays;

public enum ContractEnum {
    // 质押
    STAKING("0x0000000000000000000000000000000000000088");
    private String contractAddress;

    ContractEnum(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String contractAddress() {
        return contractAddress;
    }

    public static boolean isStaking(String contractAddress) {
        return ContractEnum.STAKING.contractAddress.equals(contractAddress);
    }

    public ContractEnum valueOfByContractAddress(String contractAddress) {
        return Arrays.stream(ContractEnum.values()).filter(item -> item.contractAddress.equals(contractAddress)).findFirst().orElse(null);
    }
}
