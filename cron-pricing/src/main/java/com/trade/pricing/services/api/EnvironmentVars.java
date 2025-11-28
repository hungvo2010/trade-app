package com.trade.pricing.services.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pricing")
@Data
public class EnvironmentVars {
    private BinanceConfig binance;
    private HuobiConfig huobi;

    @Data
    public static class BinanceConfig {
        private String baseUrl;
        private String apiVersion;
        private String priceEndpoint;
    }

    @Data
    public static class HuobiConfig {
        private String baseUrl;
        private String priceEndpoint;
    }
}
