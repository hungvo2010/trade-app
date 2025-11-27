package com.trade.pricing.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.trade.pricing.services.PriceService;

@Component
public class PricingSourcingJob implements Job {

    private PriceService priceService;

    public PricingSourcingJob(@Autowired PriceService priceService  ) {
        priceService = priceService;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        this.priceService.getLatestPrices();
    }
}
