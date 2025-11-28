package com.trade.pricing.scheduler;

import com.trade.pricing.services.BestPriceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PricingSourcingJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(PricingSourcingJob.class);
    private static final List<String> SYMBOLS = List.of("btcusdt", "ethusdt");

    private final BestPriceService bestPriceService;

    public PricingSourcingJob(BestPriceService bestPriceService) {
        this.bestPriceService = bestPriceService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Starting price aggregation job");
        try {
            bestPriceService.aggregateAndSaveBestPrices(SYMBOLS);
            logger.info("Price aggregation job completed successfully");
        } catch (Exception e) {
            logger.error("Error executing price aggregation job", e);
            throw new JobExecutionException(e);
        }
    }
}
