package com.trade.pricing.services.api;

import org.springframework.beans.factory.annotation.Value;

public class EnvironmentVars {
    @Value("${binance.base.url}")
    private String baseUrl = "";
    @Value("${binance.base.version}")
    private String apiVersion;
    @Value("${binance.price.endpoint}")
    private String priceEndpoint;
}
