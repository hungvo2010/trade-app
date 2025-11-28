package com.trade.pricing.exceptions;

public class APIExceptions extends RuntimeException {
    public APIExceptions(Exception cause, String message) {
        super(message, cause);
    }
}
