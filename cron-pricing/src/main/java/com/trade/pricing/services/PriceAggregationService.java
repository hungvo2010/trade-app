package com.trade.pricing.services;

import com.trade.pricing.entity.PriceSnapshot;
import com.trade.pricing.model.SymbolPrice;
import com.trade.pricing.repositories.PriceSnapshotRepository;
import com.trade.pricing.services.api.PricingAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceAggregationService {
    private static final Logger logger = LoggerFactory.getLogger(PriceAggregationService.class);
    
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final List<PricingAPIService> pricingServices;

    public PriceAggregationService(PriceSnapshotRepository priceSnapshotRepository, List<PricingAPIService> pricingServices) {
        this.priceSnapshotRepository = priceSnapshotRepository;
        this.pricingServices = pricingServices;
    }

    @Transactional
    public void aggregateAndSaveBestPrices(List<String> symbols) {
        for (String symbol : symbols) {
            try {
                PriceSnapshot priceSnapshot = aggregateBestPrice(symbol);
                if (priceSnapshot != null) {
                    priceSnapshotRepository.save(priceSnapshot);
                    logger.info("Saved price snapshot for {}: bid={} ({}), ask={} ({})", 
                        symbol, priceSnapshot.getBidPrice(), priceSnapshot.getBidExchange(),
                        priceSnapshot.getAskPrice(), priceSnapshot.getAskExchange());
                }
            } catch (Exception e) {
                logger.error("Error aggregating price for symbol: {}", symbol, e);
            }
        }
    }

    private PriceSnapshot aggregateBestPrice(String symbol) {
        BigDecimal bestBid = null;
        BigDecimal bestAsk = null;
        String bidExchange = null;
        String askExchange = null;

        for (PricingAPIService service : pricingServices) {
            try {
                List<SymbolPrice> prices = service.getPrice(List.of(symbol));
                if (prices.isEmpty()) continue;

                String exchangeName = service.getExchangeName();
                
                SymbolPrice matchingPrice = prices.stream()
                    .filter(p -> p.getSymbol() != null && p.getSymbol().equalsIgnoreCase(symbol))
                    .findFirst()
                    .orElse(null);

                if (matchingPrice == null) {
                    logger.warn("Symbol {} not found in response from {}", symbol, exchangeName);
                    continue;
                }

                if (matchingPrice.getBidPrice() != null) {
                    if (bestBid == null || matchingPrice.getBidPrice().compareTo(bestBid) > 0) {
                        bestBid = matchingPrice.getBidPrice();
                        bidExchange = exchangeName;
                    }
                }

                if (matchingPrice.getAskPrice() != null) {
                    if (bestAsk == null || matchingPrice.getAskPrice().compareTo(bestAsk) < 0) {
                        bestAsk = matchingPrice.getAskPrice();
                        askExchange = exchangeName;
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

        PriceSnapshot priceSnapshot = new PriceSnapshot();
        priceSnapshot.setSymbol(symbol);
        priceSnapshot.setBidPrice(bestBid);
        priceSnapshot.setAskPrice(bestAsk);
        priceSnapshot.setBidExchange(bidExchange);
        priceSnapshot.setAskExchange(askExchange);
        
        return priceSnapshot;
    }
}
