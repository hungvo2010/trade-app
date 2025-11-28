package com.trade.pricing.config;

import com.trade.pricing.env.EnvironmentVars;
import com.trade.pricing.services.api.PricingAPIService;
import com.trade.pricing.services.api.impl.BinanceAPIImpl;
import com.trade.pricing.services.api.impl.HoubiAPIImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PricingConfig {

    private final EnvironmentVars environmentVars;

    public PricingConfig(EnvironmentVars environmentVars) {
        this.environmentVars = environmentVars;
    }

    @Bean
    public PricingAPIService binanceAPIService() {
        EnvironmentVars.BinanceConfig config = environmentVars.getBinance();
        return new BinanceAPIImpl(
            config.getBaseUrl(),
            config.getApiVersion(),
            config.getPriceEndpoint()
        );
    }

    @Bean
    public PricingAPIService huobiAPIService() {
        EnvironmentVars.HuobiConfig config = environmentVars.getHuobi();
        return new HoubiAPIImpl(
            config.getBaseUrl(),
            config.getPriceEndpoint()
        );
    }
}
