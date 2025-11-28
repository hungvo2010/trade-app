package com.trade.pricing.scheduler;

import com.trade.pricing.services.api.PricingAPIService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PricingSourcingJob implements Job {

    private PricingAPIService priceService;

    public PricingSourcingJob(@Autowired PricingAPIService priceService  ) {
        priceService = priceService;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        this.priceService.getPrice(Arrays.asList("BTCUSDT", "ETHUSDT"));
    }
}
