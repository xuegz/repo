package com.atoken.blockchain.exception;

/**
 * @author dfg
 */
public class BalanceNotEnoughException extends RuntimeException {
    public BalanceNotEnoughException(Exception e) {
        super(e);
    }
}
