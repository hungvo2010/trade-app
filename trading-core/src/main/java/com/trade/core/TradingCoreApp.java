package com.trade.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.trade.core"})
@EnableJpaRepositories(basePackages = {"com.trade.core.repository"})
@EntityScan(basePackages = {"com.trade.pricing.entity"})
public class TradingCoreApp {
    public static void main(String[] args) {
        SpringApplication.run(TradingCoreApp.class, args);
    }
}
