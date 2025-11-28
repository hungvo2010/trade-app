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
        BestPriceHolder bestPrices = new BestPriceHolder();

        for (PricingAPIService service : pricingServices) {
            try {
                SymbolPrice price = fetchPriceFromService(service, symbol);
                if (price != null) {
                    updateBestPrices(bestPrices, price, service.getExchangeName());
                }
            } catch (Exception e) {
                logger.warn("Failed to fetch price from service: {}", service.getClass().getSimpleName(), e);
            }
        }

        return buildPriceSnapshot(symbol, bestPrices);
    }

    private SymbolPrice fetchPriceFromService(PricingAPIService service, String symbol) {
        List<SymbolPrice> prices = service.getPrice(List.of(symbol));
        if (prices.isEmpty()) {
            return null;
        }

        SymbolPrice matchingPrice = prices.stream()
            .filter(p -> p.getSymbol() != null && p.getSymbol().equalsIgnoreCase(symbol))
            .findFirst()
            .orElse(null);

        if (matchingPrice == null) {
            logger.warn("Symbol {} not found in response from {}", symbol, service.getExchangeName());
        }

        return matchingPrice;
    }

    private void updateBestPrices(BestPriceHolder holder, SymbolPrice price, String exchangeName) {
        if (price.getBidPrice() != null) {
            if (holder.bestBid == null || price.getBidPrice().compareTo(holder.bestBid) > 0) {
                holder.bestBid = price.getBidPrice();
                holder.bidExchange = exchangeName;
            }
        }

        if (price.getAskPrice() != null) {
            if (holder.bestAsk == null || price.getAskPrice().compareTo(holder.bestAsk) < 0) {
                holder.bestAsk = price.getAskPrice();
                holder.askExchange = exchangeName;
            }
        }
    }

    private PriceSnapshot buildPriceSnapshot(String symbol, BestPriceHolder holder) {
        if (holder.bestBid == null || holder.bestAsk == null) {
            logger.warn("Could not aggregate complete price for symbol: {}", symbol);
            return null;
        }

        PriceSnapshot priceSnapshot = new PriceSnapshot();
        priceSnapshot.setSymbol(symbol);
        priceSnapshot.setBidPrice(holder.bestBid);
        priceSnapshot.setAskPrice(holder.bestAsk);
        priceSnapshot.setBidExchange(holder.bidExchange);
        priceSnapshot.setAskExchange(holder.askExchange);
        
        return priceSnapshot;
    }

    private static class BestPriceHolder {
        BigDecimal bestBid;
        BigDecimal bestAsk;
        String bidExchange;
        String askExchange;
    }

}
