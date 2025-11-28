package com.trade.pricing.model;

public enum TransactionType {
    BUY(1),
    SELL(2);

    private int code;

    TransactionType(int code) {
        this.code = code;
    }
}
