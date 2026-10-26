package com.atoken.blockchain.http.request.account.ont;

import java.util.stream.Stream;

public enum OntMethod {
    transfer
    , transferFrom;

    public static OntMethod initOntMethod(String method) {
        return Stream.of(OntMethod.values()).filter(value -> value.name().equals(method)).findFirst().get();
    }
}
