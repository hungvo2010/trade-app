package com.trade.pricing.config;

import com.trade.pricing.scheduler.PricingSourcingJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail pricingJobDetail() {
        return JobBuilder.newJob(PricingSourcingJob.class)
                .withIdentity("pricingSourcingJob")
                .withDescription("Fetch and aggregate prices from exchanges")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger pricingJobTrigger(JobDetail pricingJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(pricingJobDetail)
                .withIdentity("pricingSourcingTrigger")
                .withDescription("Trigger every 10 seconds")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();
    }
}
