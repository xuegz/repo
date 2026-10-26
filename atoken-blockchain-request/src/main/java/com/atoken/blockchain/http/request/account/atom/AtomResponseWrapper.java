package com.atoken.blockchain.http.request.account.atom;

import java.util.List;

public class AtomResponseWrapper<T> {
    private Long height;

    private T result;

    public Long getHeight() {
        return height;
    }

    public AtomResponseWrapper<T> setHeight(Long height) {
        this.height = height;
        return this;
    }

    public T getResult() {
        return result;
    }

    public AtomResponseWrapper<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public static class AtomAmountsResponse extends AtomResponseWrapper<List<AtomAmount>> {}
    public static class AtomRewardAmountResponse extends AtomResponseWrapper<AtomRewardAmount> {}
    public static class AtomAccountResponse extends AtomResponseWrapper<AtomAccount> {}
    public static class AtomValidatorsResponse extends AtomResponseWrapper<List<AtomValidator>> {}
    public static class AtomStakingPoolResponse extends AtomResponseWrapper<AtomStakingPool> {}
    public static class AtomDelegatorsResponse extends AtomResponseWrapper<List<AtomDelegator>> {}
    public static class DoubleResponse extends AtomResponseWrapper<Double> {}
    public static class AtomDelegationResponse extends AtomResponseWrapper<AtomDelegation> {}
    public static class AtomDelegationsResponse extends AtomResponseWrapper<List<AtomDelegationResult>> {}
    public static class AtomUnbondingDelegationResponse extends AtomResponseWrapper<AtomUnbondingDelegation> {}
    public static class AtomUnbondingDelegationsResponse extends AtomResponseWrapper<List<AtomUnbondingDelegation>> {}
    public static class AtomStakingInfoResponse extends AtomResponseWrapper<AtomStakingInfo> {}
    public static class AtomMintingInfoResponse extends AtomResponseWrapper<AtomMintingInfo> {}
    public static class AtomValidatorResponse extends AtomResponseWrapper<AtomValidator> {}

}
