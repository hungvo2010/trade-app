package com.trade.pricing.services;

import com.trade.pricing.entity.BestPrice;
import com.trade.pricing.model.SymbolPrice;
import com.trade.pricing.repositories.BestPriceRepository;
import com.trade.pricing.services.api.PricingAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BestPriceService {
    private static final Logger logger = LoggerFactory.getLogger(BestPriceService.class);
    
    private final BestPriceRepository bestPriceRepository;
    private final List<PricingAPIService> pricingServices;

    public BestPriceService(BestPriceRepository bestPriceRepository, List<PricingAPIService> pricingServices) {
        this.bestPriceRepository = bestPriceRepository;
        this.pricingServices = pricingServices;
    }

    @Transactional
    public void aggregateAndSaveBestPrices(List<String> symbols) {
        for (String symbol : symbols) {
            try {
                BestPrice bestPrice = aggregateBestPrice(symbol);
                if (bestPrice != null) {
                    bestPriceRepository.save(bestPrice);
                    logger.info("Saved best price for {}: bid={} ({}), ask={} ({})", 
                        symbol, bestPrice.getBestBidPrice(), bestPrice.getBidSource(),
                        bestPrice.getBestAskPrice(), bestPrice.getAskSource());
                }
            } catch (Exception e) {
                logger.error("Error aggregating price for symbol: {}", symbol, e);
            }
        }
    }

    private BestPrice aggregateBestPrice(String symbol) {
        BigDecimal bestBid = null;
        BigDecimal bestAsk = null;
        String bidSource = null;
        String askSource = null;

        for (PricingAPIService service : pricingServices) {
            try {
                List<SymbolPrice> prices = service.getPrice(List.of(symbol));
                if (prices.isEmpty()) continue;

                SymbolPrice price = prices.get(0);
                String sourceName = service.getClass().getSimpleName().replace("APIImpl", "");

                if (price.getBidPrice() != null) {
                    if (bestBid == null || price.getBidPrice().compareTo(bestBid) > 0) {
                        bestBid = price.getBidPrice();
                        bidSource = sourceName;
                    }
                }

                if (price.getAskPrice() != null) {
                    if (bestAsk == null || price.getAskPrice().compareTo(bestAsk) < 0) {
                        bestAsk = price.getAskPrice();
                        askSource = sourceName;
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to fetch price from service: {}", service.getClass().getSimpleName(), e);
            }
        }

        if (bestBid == null || bestAsk == null) {
            logger.warn("Could not aggregate complete price for symbol: {}", symbol);
            return null;
        }

        BestPrice bestPrice = new BestPrice();
        bestPrice.setSymbol(symbol);
        bestPrice.setBestBidPrice(bestBid);
        bestPrice.setBestAskPrice(bestAsk);
        bestPrice.setBidSource(bidSource);
        bestPrice.setAskSource(askSource);
        
        return bestPrice;
    }
}
